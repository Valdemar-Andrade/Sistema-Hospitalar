package com.hospital.sistema.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.dto.ConsultaRequest;
import com.hospital.sistema.entity.*;
import com.hospital.sistema.enums.StatusConsulta;
import com.hospital.sistema.enums.TipoDocumento;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração E2E para o fluxo de Consultas.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integração - Consulta")
class ConsultaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Autowired
    private TipoConsultaRepository tipoConsultaRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @MockBean
    private SessaoUsuario sessaoUsuario;

    private Paciente paciente;
    private Medico medico;
    private TipoConsulta tipoConsulta;

    @BeforeEach
    void setUp() {
        // Configurar mock da sessão como admin
        when(sessaoUsuario.isLogado()).thenReturn(true);
        when(sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo())).thenReturn(true);
        when(sessaoUsuario.getNomeUsuario()).thenReturn("Admin");
        when(sessaoUsuario.getIdUsuario()).thenReturn(1L);

        // Criar especialidade
        Especialidade especialidade = new Especialidade();
        especialidade.setNome("Clínica Geral");
        especialidade = especialidadeRepository.save(especialidade);

        // Criar médico
        medico = new Medico();
        medico.setNome("Dr. Teste");
        medico.setLogin("dr.teste");
        medico.setSenha("senha123");
        medico.setEspecialidade(especialidade);
        medico = medicoRepository.save(medico);

        // Criar paciente
        paciente = new Paciente();
        paciente.setNome("Paciente Teste");
        paciente.setDocumento(new Documento(TipoDocumento.BILHETE_IDENTIDADE, "333333333CC333"));
        paciente.setDataNascimento(LocalDate.of(1990, 1, 1));
        paciente = pacienteRepository.save(paciente);

        // Criar tipo de consulta
        tipoConsulta = new TipoConsulta();
        tipoConsulta.setNome("Consulta Rotina");
        tipoConsulta = tipoConsultaRepository.save(tipoConsulta);
    }

    @Nested
    @DisplayName("Agendamento de consultas")
    class AgendamentoTests {

        @Test
        @DisplayName("Deve agendar consulta com sucesso")
        void deveAgendarConsultaComSucesso() throws Exception {
            // Arrange
            ConsultaRequest request = new ConsultaRequest();
            request.setPacienteId(paciente.getId());
            request.setMedicoId(medico.getId());
            request.setTipoConsultaId(tipoConsulta.getId());
            request.setDiaSemana("MONDAY");
            request.setHoraInicio("09:00");
            request.setHoraFim("09:30");

            // Act & Assert
            mockMvc.perform(post("/consultas/agendar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem").value("Consulta agendada com sucesso"))
                    .andExpect(jsonPath("$.consultaId").exists());
        }
    }

    @Nested
    @DisplayName("Consultas de hoje")
    class ConsultasHojeTests {

        @Test
        @DisplayName("Deve listar consultas agendadas para hoje")
        void deveListarConsultasDeHoje() throws Exception {
            // Arrange - criar consulta para hoje
            Consulta consulta = new Consulta();
            consulta.setPaciente(paciente);
            consulta.setMedico(medico);
            consulta.setTipoConsulta(tipoConsulta);
            consulta.setData(LocalDate.now());
            consulta.setHoraInicio(LocalTime.of(10, 0));
            consulta.setHoraFim(LocalTime.of(10, 30));
            consulta.setStatus(StatusConsulta.AGENDADA);
            consultaRepository.save(consulta);

            // Act & Assert
            mockMvc.perform(get("/consultas/hoje"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].paciente").value("Paciente Teste"))
                    .andExpect(jsonPath("$[0].medico").value("Dr. Teste"));
        }
    }

    @Nested
    @DisplayName("Histórico de consultas")
    class HistoricoTests {

        @Test
        @DisplayName("Deve buscar histórico de consultas do paciente")
        void deveBuscarHistoricoDoPaciente() throws Exception {
            // Arrange
            Consulta consulta = new Consulta();
            consulta.setPaciente(paciente);
            consulta.setMedico(medico);
            consulta.setTipoConsulta(tipoConsulta);
            consulta.setData(LocalDate.now().minusDays(5));
            consulta.setHoraInicio(LocalTime.of(14, 0));
            consulta.setHoraFim(LocalTime.of(14, 30));
            consulta.setStatus(StatusConsulta.REALIZADA);
            consultaRepository.save(consulta);

            // Act & Assert
            mockMvc.perform(get("/consultas/paciente/" + paciente.getId() + "/historico"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("Encaminhamento para triagem")
    class EncaminhamentoTriagemTests {

        @Test
        @DisplayName("Deve encaminhar paciente para triagem")
        void deveEncaminharParaTriagem() throws Exception {
            // Arrange
            Consulta consulta = new Consulta();
            consulta.setPaciente(paciente);
            consulta.setMedico(medico);
            consulta.setTipoConsulta(tipoConsulta);
            consulta.setData(LocalDate.now());
            consulta.setHoraInicio(LocalTime.of(11, 0));
            consulta.setHoraFim(LocalTime.of(11, 30));
            consulta.setStatus(StatusConsulta.AGENDADA);
            consulta = consultaRepository.save(consulta);

            // Act & Assert
            mockMvc.perform(post("/consultas/" + consulta.getId() + "/encaminhar-triagem"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem").value("Paciente encaminhado para triagem"));
        }
    }
}
