package com.hospital.sistema.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.dto.PacienteRequest;
import com.hospital.sistema.dto.PacienteResponse;
import com.hospital.sistema.entity.Documento;
import com.hospital.sistema.entity.Paciente;
import com.hospital.sistema.enums.TipoDocumento;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.service.PacienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes do PacienteController usando @WebMvcTest.
 */
@WebMvcTest(PacienteController.class)
@DisplayName("PacienteController")
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PacienteService pacienteService;

    @MockBean
    private SessaoUsuario sessaoUsuario;

    private Paciente paciente;
    private PacienteRequest pacienteRequest;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("João Silva");
        paciente.setDocumento(new Documento(TipoDocumento.BILHETE_IDENTIDADE, "123456789AB123"));
        paciente.setTelefone("912345678");
        paciente.setEmail("joao@email.com");
        paciente.setDataNascimento(LocalDate.of(1990, 5, 15));

        pacienteRequest = new PacienteRequest();
        pacienteRequest.setNome("João Silva");
        pacienteRequest.setTipoDocumento("BILHETE_IDENTIDADE");
        pacienteRequest.setNumeroDocumento("123456789AB123");
        pacienteRequest.setTelefone("912345678");
        pacienteRequest.setEmail("joao@email.com");
        pacienteRequest.setDataNascimento("1990-05-15");
    }

    @Nested
    @DisplayName("GET /admin/pacientes")
    class ListarPacientesTests {

        @Test
        @DisplayName("Deve listar pacientes quando usuário é admin")
        void deveListarPacientesQuandoAdmin() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo())).thenReturn(true);

            Page<Paciente> page = new PageImpl<>(Arrays.asList(paciente));
            when(pacienteService.listarTodos(any(Pageable.class))).thenReturn(page);

            // Act & Assert
            mockMvc.perform(get("/admin/pacientes")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/pacientes/pacientes"))
                    .andExpect(model().attributeExists("pacientes"));
        }

        @Test
        @DisplayName("Deve redirecionar quando usuário não é admin")
        void deveRedirecionarQuandoNaoAdmin() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo())).thenReturn(false);

            // Act & Assert
            mockMvc.perform(get("/admin/pacientes"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));
        }
    }

    @Nested
    @DisplayName("POST /admin/pacientes")
    class SalvarPacienteTests {

        @Test
        @DisplayName("Deve salvar paciente quando usuário autorizado")
        void deveSalvarPacienteQuandoAutorizado() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo())).thenReturn(true);
            when(pacienteService.salvar(any(PacienteRequest.class))).thenReturn(paciente);

            // Act & Assert
            mockMvc.perform(post("/admin/pacientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pacienteRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("João Silva"));
        }

        @Test
        @DisplayName("Deve retornar 401 quando usuário não autorizado")
        void deveRetornar401QuandoNaoAutorizado() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(false);

            // Act & Assert
            mockMvc.perform(post("/admin/pacientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pacienteRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /admin/pacientes/{id}/editar")
    class BuscarPacienteParaEdicaoTests {

        @Test
        @DisplayName("Deve retornar paciente para edição")
        void deveRetornarPacienteParaEdicao() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(pacienteService.buscarPorId(1L)).thenReturn(paciente);

            // Act & Assert
            mockMvc.perform(get("/admin/pacientes/1/editar"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("João Silva"));
        }

        @Test
        @DisplayName("Deve retornar 401 quando não logado")
        void deveRetornar401QuandoNaoLogado() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(false);

            // Act & Assert
            mockMvc.perform(get("/admin/pacientes/1/editar"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /admin/pacientes/buscar")
    class BuscarPacientesPorNomeTests {

        @Test
        @DisplayName("Deve buscar pacientes por nome")
        void deveBuscarPacientesPorNome() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            List<PacienteResponse> responses = Arrays.asList(
                    new PacienteResponse(1L, "João Silva", "123456789AB123", "912345678")
            );
            when(pacienteService.buscarPorNome("João")).thenReturn(responses);

            // Act & Assert
            mockMvc.perform(get("/admin/pacientes/buscar")
                            .param("term", "João"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nome").value("João Silva"));
        }
    }

    @Nested
    @DisplayName("PUT /admin/pacientes/{id}/atualizar")
    class AtualizarPacienteTests {

        @Test
        @DisplayName("Deve atualizar paciente quando admin")
        void deveAtualizarPacienteQuandoAdmin() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo())).thenReturn(true);
            when(pacienteService.atualizar(anyLong(), any(PacienteRequest.class))).thenReturn(paciente);

            // Act & Assert
            mockMvc.perform(put("/admin/pacientes/1/atualizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pacienteRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("João Silva"));
        }

        @Test
        @DisplayName("Deve retornar 401 quando não é admin")
        void deveRetornar401QuandoNaoAdmin() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo())).thenReturn(false);

            // Act & Assert
            mockMvc.perform(put("/admin/pacientes/1/atualizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pacienteRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("DELETE /admin/pacientes/{id}")
    class ExcluirPacienteTests {

        @Test
        @DisplayName("Deve excluir paciente quando admin")
        void deveExcluirPacienteQuandoAdmin() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo())).thenReturn(true);
            doNothing().when(pacienteService).deletar(1L);

            // Act & Assert
            mockMvc.perform(delete("/admin/pacientes/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem").value("Paciente excluído com sucesso"));
        }

        @Test
        @DisplayName("Deve retornar 401 quando não autorizado")
        void deveRetornar401QuandoNaoAutorizado() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(false);

            // Act & Assert
            mockMvc.perform(delete("/admin/pacientes/1"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
