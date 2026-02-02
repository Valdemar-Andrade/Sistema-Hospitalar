package com.hospital.sistema.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.dto.PacienteRequest;
import com.hospital.sistema.entity.Documento;
import com.hospital.sistema.entity.Paciente;
import com.hospital.sistema.enums.TipoDocumento;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.repository.PacienteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração E2E para o fluxo completo de Paciente.
 * Usa @SpringBootTest para carregar todo o contexto da aplicação.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integração - Paciente")
class PacienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PacienteRepository pacienteRepository;

    @MockBean
    private SessaoUsuario sessaoUsuario;

    private PacienteRequest pacienteRequest;

    @BeforeEach
    void setUp() {
        // Configurar mock da sessão como admin para os testes
        when(sessaoUsuario.isLogado()).thenReturn(true);
        when(sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo())).thenReturn(true);
        when(sessaoUsuario.getNomeUsuario()).thenReturn("Admin");
        when(sessaoUsuario.getIdUsuario()).thenReturn(1L);
        when(sessaoUsuario.getTipoUsuario()).thenReturn(TipoUsuario.ADMIN.getTipo());

        // Criar request de paciente
        pacienteRequest = new PacienteRequest();
        pacienteRequest.setNome("João Integração");
        pacienteRequest.setTipoDocumento("BILHETE_IDENTIDADE");
        pacienteRequest.setNumeroDocumento("111111111AA111");
        pacienteRequest.setTelefone("912345678");
        pacienteRequest.setEmail("joao.integracao@email.com");
        pacienteRequest.setDataNascimento("1990-05-15");
        pacienteRequest.setEndereco("Rua de Teste, 123");
    }

    @Nested
    @DisplayName("Fluxo completo de CRUD")
    class FluxoCrudTests {

        @Test
        @DisplayName("Deve criar, buscar, atualizar e deletar paciente")
        void deveCriarBuscarAtualizarEDeletar() throws Exception {
            // 1. CRIAR paciente
            String response = mockMvc.perform(post("/admin/pacientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pacienteRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.nome").value("João Integração"))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Extrair ID do paciente criado
            Long pacienteId = objectMapper.readTree(response).get("id").asLong();

            // 2. BUSCAR paciente criado
            mockMvc.perform(get("/admin/pacientes/" + pacienteId + "/editar"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("João Integração"));

            // 3. ATUALIZAR paciente
            pacienteRequest.setNome("João Atualizado");
            mockMvc.perform(put("/admin/pacientes/" + pacienteId + "/atualizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pacienteRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("João Atualizado"));

            // 4. DELETAR paciente
            mockMvc.perform(delete("/admin/pacientes/" + pacienteId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem").value("Paciente excluído com sucesso"));

            // 5. VERIFICAR que foi deletado
            assertThat(pacienteRepository.findById(pacienteId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Busca de pacientes")
    class BuscaPacientesTests {

        @Test
        @DisplayName("Deve buscar pacientes por nome parcial")
        void deveBuscarPacientesPorNomeParcial() throws Exception {
            // Arrange - criar paciente
            Paciente paciente = new Paciente();
            paciente.setNome("Maria da Silva");
            paciente.setDocumento(new Documento(TipoDocumento.BILHETE_IDENTIDADE, "222222222BB222"));
            paciente.setDataNascimento(LocalDate.of(1985, 3, 20));
            pacienteRepository.save(paciente);

            // Act & Assert
            mockMvc.perform(get("/admin/pacientes/buscar")
                            .param("term", "Maria"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nome").value("Maria da Silva"));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não encontra")
        void deveRetornarListaVaziaQuandoNaoEncontra() throws Exception {
            mockMvc.perform(get("/admin/pacientes/buscar")
                            .param("term", "NomeInexistente123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("Validações de negócio")
    class ValidacoesNegocioTests {

        @Test
        @DisplayName("Deve rejeitar documento já cadastrado")
        void deveRejeitarDocumentoJaCadastrado() throws Exception {
            // Criar primeiro paciente
            mockMvc.perform(post("/admin/pacientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pacienteRequest)))
                    .andExpect(status().isOk());

            // Tentar criar segundo paciente com mesmo documento
            PacienteRequest duplicado = new PacienteRequest();
            duplicado.setNome("Outro Paciente");
            duplicado.setTipoDocumento("BILHETE_IDENTIDADE");
            duplicado.setNumeroDocumento("111111111AA111"); // Mesmo documento
            duplicado.setTelefone("987654321");
            duplicado.setDataNascimento("1995-01-01");

            mockMvc.perform(post("/admin/pacientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicado)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Deve rejeitar data de nascimento futura")
        void deveRejeitarDataNascimentoFutura() throws Exception {
            pacienteRequest.setDataNascimento("2030-01-01");

            mockMvc.perform(post("/admin/pacientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pacienteRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve rejeitar documento com formato inválido")
        void deveRejeitarDocumentoFormatoInvalido() throws Exception {
            pacienteRequest.setNumeroDocumento("INVALIDO");

            mockMvc.perform(post("/admin/pacientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pacienteRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Controle de acesso")
    class ControleAcessoTests {

        @Test
        @DisplayName("Deve negar acesso quando não logado")
        void deveNegarAcessoQuandoNaoLogado() throws Exception {
            // Limpar sessão - reconfigurar mock
            when(sessaoUsuario.isLogado()).thenReturn(false);
            when(sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo())).thenReturn(false);

            mockMvc.perform(post("/admin/pacientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pacienteRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Recepcionista pode cadastrar pacientes")
        void recepcionistaPodeCadastrarPacientes() throws Exception {
            // Configurar como recepcionista
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo())).thenReturn(false);
            when(sessaoUsuario.isTipoUsuario(TipoUsuario.RECEPCIONISTA.getTipo())).thenReturn(true);

            mockMvc.perform(post("/admin/pacientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pacienteRequest)))
                    .andExpect(status().isOk());
        }
    }
}
