package com.hospital.sistema.service;

import com.hospital.sistema.dto.ConsultaRequest;
import com.hospital.sistema.dto.ConsultaResponse;
import com.hospital.sistema.entity.*;
import com.hospital.sistema.enums.StatusConsulta;
import com.hospital.sistema.exception.RecursoNaoEncontradoException;
import com.hospital.sistema.repository.ConsultaRepository;
import com.hospital.sistema.repository.TipoConsultaRepository;
import com.hospital.sistema.util.FilaTriagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ConsultaService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultaService")
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private PacienteService pacienteService;

    @Mock
    private MedicoService medicoService;

    @Mock
    private TipoConsultaRepository tipoConsultaRepository;

    @Mock
    private FilaTriagem filaTriagem;

    @InjectMocks
    private ConsultaService consultaService;

    private Consulta consulta;
    private Paciente paciente;
    private Medico medico;
    private TipoConsulta tipoConsulta;
    private Especialidade especialidade;

    @BeforeEach
    void setUp() {
        especialidade = new Especialidade();
        especialidade.setId(1L);
        especialidade.setNome("Clínica Geral");

        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("João Silva");

        medico = new Medico();
        medico.setId(1L);
        medico.setNome("Dr. Carlos");
        medico.setEspecialidade(especialidade);

        tipoConsulta = new TipoConsulta();
        tipoConsulta.setId(1L);
        tipoConsulta.setNome("Consulta Rotina");

        consulta = new Consulta();
        consulta.setId(1L);
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setTipoConsulta(tipoConsulta);
        consulta.setData(LocalDate.now());
        consulta.setHoraInicio(LocalTime.of(9, 0));
        consulta.setHoraFim(LocalTime.of(9, 30));
        consulta.setStatus(StatusConsulta.AGENDADA);
    }

    @Nested
    @DisplayName("Buscar por ID")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Deve retornar consulta quando ID existe")
        void deveRetornarConsultaQuandoIdExiste() {
            // Arrange
            when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

            // Act
            Consulta resultado = consultaService.buscarPorId(1L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            // Arrange
            when(consultaRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> consultaService.buscarPorId(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }
    }

    @Nested
    @DisplayName("Buscar consultas de hoje")
    class BuscarConsultasDeHojeTests {

        @Test
        @DisplayName("Deve retornar consultas agendadas para hoje")
        void deveRetornarConsultasDeHoje() {
            // Arrange
            when(consultaRepository.buscarPorDataEStatus(any(LocalDate.class), eq(StatusConsulta.AGENDADA)))
                    .thenReturn(Arrays.asList(consulta));

            // Act
            List<ConsultaResponse> resultado = consultaService.buscarConsultasDeHoje();

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getPaciente()).isEqualTo("João Silva");
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há consultas")
        void deveRetornarListaVaziaQuandoNaoHaConsultas() {
            // Arrange
            when(consultaRepository.buscarPorDataEStatus(any(LocalDate.class), eq(StatusConsulta.AGENDADA)))
                    .thenReturn(List.of());

            // Act
            List<ConsultaResponse> resultado = consultaService.buscarConsultasDeHoje();

            // Assert
            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Buscar histórico do paciente")
    class BuscarHistoricoPacienteTests {

        @Test
        @DisplayName("Deve retornar histórico de consultas do paciente")
        void deveRetornarHistoricoDoPaciente() {
            // Arrange
            when(consultaRepository.findByPacienteIdOrderByDataDesc(1L))
                    .thenReturn(Arrays.asList(consulta));

            // Act
            List<Consulta> resultado = consultaService.buscarHistoricoPaciente(1L);

            // Assert
            assertThat(resultado).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Agendar consulta")
    class AgendarTests {

        @Test
        @DisplayName("Deve agendar consulta com sucesso")
        void deveAgendarConsultaComSucesso() {
            // Arrange
            ConsultaRequest request = new ConsultaRequest();
            request.setPacienteId(1L);
            request.setMedicoId(1L);
            request.setTipoConsultaId(1L);
            request.setDiaSemana("MONDAY");
            request.setHoraInicio("09:00");
            request.setHoraFim("09:30");

            when(medicoService.buscarPorId(1L)).thenReturn(medico);
            when(pacienteService.buscarPorId(1L)).thenReturn(paciente);
            when(tipoConsultaRepository.findById(1L)).thenReturn(Optional.of(tipoConsulta));
            when(consultaRepository.save(any(Consulta.class))).thenReturn(consulta);

            // Act
            Optional<Consulta> resultado = consultaService.agendar(request);

            // Assert
            assertThat(resultado).isPresent();
            verify(consultaRepository).save(any(Consulta.class));
        }

        @Test
        @DisplayName("Deve retornar empty quando ocorre erro")
        void deveRetornarEmptyQuandoOcorreErro() {
            // Arrange
            ConsultaRequest request = new ConsultaRequest();
            request.setPacienteId(999L);
            request.setMedicoId(1L);
            request.setTipoConsultaId(1L);
            request.setDiaSemana("MONDAY");
            request.setHoraInicio("09:00");
            request.setHoraFim("09:30");

            when(medicoService.buscarPorId(1L)).thenReturn(medico);
            when(pacienteService.buscarPorId(999L)).thenThrow(new RecursoNaoEncontradoException("Paciente", 999L));

            // Act
            Optional<Consulta> resultado = consultaService.agendar(request);

            // Assert
            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Encaminhar para triagem")
    class EncaminharParaTriagemTests {

        @Test
        @DisplayName("Deve encaminhar paciente para triagem com sucesso")
        void deveEncaminharParaTriagemComSucesso() {
            // Arrange
            when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

            // Act
            boolean resultado = consultaService.encaminharParaTriagem(1L);

            // Assert
            assertThat(resultado).isTrue();
            verify(filaTriagem).adicionar(paciente);
        }

        @Test
        @DisplayName("Deve retornar false quando consulta não existe")
        void deveRetornarFalseQuandoConsultaNaoExiste() {
            // Arrange
            when(consultaRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            boolean resultado = consultaService.encaminharParaTriagem(999L);

            // Assert
            assertThat(resultado).isFalse();
            verify(filaTriagem, never()).adicionar(any());
        }
    }

    @Nested
    @DisplayName("Buscar consulta agendada por paciente")
    class BuscarConsultaAgendadaPorPacienteTests {

        @Test
        @DisplayName("Deve retornar última consulta agendada do paciente")
        void deveRetornarUltimaConsultaAgendada() {
            // Arrange
            when(consultaRepository.buscarPorPacienteEStatus(1L, StatusConsulta.AGENDADA))
                    .thenReturn(Arrays.asList(consulta));

            // Act
            Optional<Consulta> resultado = consultaService.buscarConsultaAgendadaPorPaciente(1L);

            // Assert
            assertThat(resultado).isPresent();
        }

        @Test
        @DisplayName("Deve retornar empty quando não há consultas agendadas")
        void deveRetornarEmptyQuandoNaoHaConsultas() {
            // Arrange
            when(consultaRepository.buscarPorPacienteEStatus(1L, StatusConsulta.AGENDADA))
                    .thenReturn(List.of());

            // Act
            Optional<Consulta> resultado = consultaService.buscarConsultaAgendadaPorPaciente(1L);

            // Assert
            assertThat(resultado).isEmpty();
        }
    }
}
