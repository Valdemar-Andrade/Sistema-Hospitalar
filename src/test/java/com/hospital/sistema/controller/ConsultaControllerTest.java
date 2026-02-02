package com.hospital.sistema.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.dto.ConsultaRequest;
import com.hospital.sistema.dto.ConsultaResponse;
import com.hospital.sistema.entity.Consulta;
import com.hospital.sistema.service.ConsultaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes do ConsultaController usando @WebMvcTest.
 */
@WebMvcTest(ConsultaController.class)
@DisplayName("ConsultaController")
class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConsultaService consultaService;

    @MockBean
    private SessaoUsuario sessaoUsuario;

    private ConsultaResponse consultaResponse;
    private Consulta consulta;

    @BeforeEach
    void setUp() {
        consultaResponse = new ConsultaResponse();
        consultaResponse.setId(1L);
        consultaResponse.setPaciente("João Silva");
        consultaResponse.setMedico("Dr. Carlos");
        consultaResponse.setEspecialidade("Clínica Geral");
        consultaResponse.setTipoConsulta("Consulta Rotina");
        consultaResponse.setData(LocalDate.now());
        consultaResponse.setHoraInicio(LocalTime.of(9, 0));
        consultaResponse.setHoraFim(LocalTime.of(9, 30));
        consultaResponse.setStatus("Agendada");

        consulta = new Consulta();
        consulta.setId(1L);
    }

    @Nested
    @DisplayName("GET /consultas/hoje")
    class ListarConsultasDeHojeTests {

        @Test
        @DisplayName("Deve listar consultas de hoje quando logado")
        void deveListarConsultasDeHoje() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            List<ConsultaResponse> consultas = Arrays.asList(consultaResponse);
            when(consultaService.buscarConsultasDeHoje()).thenReturn(consultas);

            // Act & Assert
            mockMvc.perform(get("/consultas/hoje"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].paciente").value("João Silva"))
                    .andExpect(jsonPath("$[0].medico").value("Dr. Carlos"));
        }

        @Test
        @DisplayName("Deve retornar 401 quando não logado")
        void deveRetornar401QuandoNaoLogado() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(false);

            // Act & Assert
            mockMvc.perform(get("/consultas/hoje"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /consultas/agendar")
    class AgendarConsultaTests {

        @Test
        @DisplayName("Deve agendar consulta com sucesso")
        void deveAgendarConsultaComSucesso() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(consultaService.agendar(any(ConsultaRequest.class))).thenReturn(Optional.of(consulta));

            ConsultaRequest request = new ConsultaRequest();
            request.setPacienteId(1L);
            request.setMedicoId(1L);
            request.setTipoConsultaId(1L);
            request.setDiaSemana("MONDAY");
            request.setHoraInicio("09:00");
            request.setHoraFim("09:30");

            // Act & Assert
            mockMvc.perform(post("/consultas/agendar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem").value("Consulta agendada com sucesso"))
                    .andExpect(jsonPath("$.consultaId").value(1));
        }

        @Test
        @DisplayName("Deve retornar erro quando agendamento falha")
        void deveRetornarErroQuandoAgendamentoFalha() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(consultaService.agendar(any(ConsultaRequest.class))).thenReturn(Optional.empty());

            ConsultaRequest request = new ConsultaRequest();
            request.setPacienteId(1L);
            request.setMedicoId(1L);
            request.setTipoConsultaId(1L);
            request.setDiaSemana("MONDAY");
            request.setHoraInicio("09:00");
            request.setHoraFim("09:30");

            // Act & Assert
            mockMvc.perform(post("/consultas/agendar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.erro").exists());
        }

        @Test
        @DisplayName("Deve retornar 401 quando não logado")
        void deveRetornar401QuandoNaoLogado() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(false);

            ConsultaRequest request = new ConsultaRequest();
            request.setPacienteId(1L);
            request.setMedicoId(1L);
            request.setTipoConsultaId(1L);
            request.setDiaSemana("MONDAY");
            request.setHoraInicio("09:00");
            request.setHoraFim("09:30");

            // Act & Assert
            mockMvc.perform(post("/consultas/agendar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /consultas/{id}/encaminhar-triagem")
    class EncaminharParaTriagemTests {

        @Test
        @DisplayName("Deve encaminhar para triagem com sucesso")
        void deveEncaminharParaTriagemComSucesso() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(consultaService.encaminharParaTriagem(1L)).thenReturn(true);

            // Act & Assert
            mockMvc.perform(post("/consultas/1/encaminhar-triagem"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem").value("Paciente encaminhado para triagem"));
        }

        @Test
        @DisplayName("Deve retornar erro quando consulta não encontrada")
        void deveRetornarErroQuandoConsultaNaoEncontrada() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(consultaService.encaminharParaTriagem(999L)).thenReturn(false);

            // Act & Assert
            mockMvc.perform(post("/consultas/999/encaminhar-triagem"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.erro").value("Consulta não encontrada"));
        }
    }

    @Nested
    @DisplayName("GET /consultas/paciente/{pacienteId}/historico")
    class BuscarHistoricoPacienteTests {

        @Test
        @DisplayName("Deve buscar histórico do paciente")
        void deveBuscarHistoricoDoPaciente() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(true);
            when(consultaService.buscarHistoricoPaciente(1L)).thenReturn(Arrays.asList(consulta));

            // Act & Assert
            mockMvc.perform(get("/consultas/paciente/1/historico"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        @DisplayName("Deve retornar 401 quando não logado")
        void deveRetornar401QuandoNaoLogado() throws Exception {
            // Arrange
            when(sessaoUsuario.isLogado()).thenReturn(false);

            // Act & Assert
            mockMvc.perform(get("/consultas/paciente/1/historico"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
