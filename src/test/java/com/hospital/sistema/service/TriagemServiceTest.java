package com.hospital.sistema.service;

import com.hospital.sistema.dto.TriagemRequest;
import com.hospital.sistema.entity.*;
import com.hospital.sistema.enums.NivelUrgencia;
import com.hospital.sistema.enums.StatusConsulta;
import com.hospital.sistema.exception.RecursoNaoEncontradoException;
import com.hospital.sistema.repository.EnfermeiroRepository;
import com.hospital.sistema.repository.TriagemRepository;
import com.hospital.sistema.util.FilaAtendimentoMedico;
import com.hospital.sistema.util.FilaTriagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para TriagemService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TriagemService")
class TriagemServiceTest {

    @Mock
    private TriagemRepository triagemRepository;

    @Mock
    private PacienteService pacienteService;

    @Mock
    private ConsultaService consultaService;

    @Mock
    private EnfermeiroRepository enfermeiroRepository;

    @Mock
    private FilaTriagem filaTriagem;

    @Mock
    private FilaAtendimentoMedico filaAtendimentoMedico;

    @InjectMocks
    private TriagemService triagemService;

    private Paciente paciente;
    private Enfermeiro enfermeiro;
    private Triagem triagem;
    private TriagemRequest triagemRequest;
    private Medico medico;
    private Consulta consulta;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("João Silva");

        enfermeiro = new Enfermeiro();
        enfermeiro.setId(1L);
        enfermeiro.setNome("Enf. Ana");

        medico = new Medico();
        medico.setId(1L);
        medico.setNome("Dr. Carlos");

        consulta = new Consulta();
        consulta.setId(1L);
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setStatus(StatusConsulta.AGENDADA);

        triagem = new Triagem();
        triagem.setId(1L);
        triagem.setPaciente(paciente);
        triagem.setEnfermeiro(enfermeiro);
        triagem.setNivelUrgencia(NivelUrgencia.URGENTE);

        triagemRequest = new TriagemRequest();
        triagemRequest.setPacienteId(1L);
        triagemRequest.setPeso(70.0);
        triagemRequest.setAltura(1.75);
        triagemRequest.setTemperatura(36.5);
        triagemRequest.setPressaoArterial("120/80");
        triagemRequest.setFrequenciaCardiaca(72);
        triagemRequest.setSintomas("Dor de cabeça");
        triagemRequest.setObservacoes("Paciente estável");
        triagemRequest.setNivelUrgencia("URGENTE");
    }

    @Nested
    @DisplayName("Listar fila de triagem")
    class ListarFilaTriagemTests {

        @Test
        @DisplayName("Deve retornar lista de pacientes na fila")
        void deveRetornarListaDePacientes() {
            // Arrange
            when(filaTriagem.listarPacientes()).thenReturn(Arrays.asList(paciente));

            // Act
            List<Paciente> resultado = triagemService.listarFilaTriagem();

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");
        }
    }

    @Nested
    @DisplayName("Buscar histórico do paciente")
    class BuscarHistoricoTests {

        @Test
        @DisplayName("Deve retornar histórico de triagens do paciente")
        void deveRetornarHistoricoDeTriagens() {
            // Arrange
            when(triagemRepository.findByPacienteIdOrderByDataHoraDesc(1L))
                    .thenReturn(Arrays.asList(triagem));

            // Act
            List<Triagem> resultado = triagemService.buscarHistoricoPaciente(1L);

            // Assert
            assertThat(resultado).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Realizar triagem")
    class RealizarTriagemTests {

        @Test
        @DisplayName("Deve realizar triagem com sucesso sem consulta")
        void deveRealizarTriagemSemConsulta() {
            // Arrange
            when(pacienteService.buscarPorId(1L)).thenReturn(paciente);
            when(enfermeiroRepository.findById(1L)).thenReturn(Optional.of(enfermeiro));
            when(triagemRepository.save(any(Triagem.class))).thenReturn(triagem);

            // Act
            Triagem resultado = triagemService.realizar(triagemRequest, 1L);

            // Assert
            assertThat(resultado).isNotNull();
            verify(triagemRepository).save(any(Triagem.class));
            verify(filaTriagem).remover(1L);
        }

        @Test
        @DisplayName("Deve realizar triagem com consulta e encaminhar para fila do médico")
        void deveRealizarTriagemComConsulta() {
            // Arrange
            triagemRequest.setConsultaId(1L);

            when(pacienteService.buscarPorId(1L)).thenReturn(paciente);
            when(enfermeiroRepository.findById(1L)).thenReturn(Optional.of(enfermeiro));
            when(triagemRepository.save(any(Triagem.class))).thenReturn(triagem);
            when(consultaService.buscarPorId(1L)).thenReturn(consulta);
            when(consultaService.salvar(any(Consulta.class))).thenReturn(consulta);

            // Act
            Triagem resultado = triagemService.realizar(triagemRequest, 1L);

            // Assert
            assertThat(resultado).isNotNull();
            verify(consultaService).salvar(any(Consulta.class));
            verify(filaAtendimentoMedico).adicionar(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando enfermeiro não existe")
        void deveLancarExcecaoQuandoEnfermeiroNaoExiste() {
            // Arrange
            when(pacienteService.buscarPorId(1L)).thenReturn(paciente);
            when(enfermeiroRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> triagemService.realizar(triagemRequest, 999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }

        @Test
        @DisplayName("Deve lançar exceção quando paciente não existe")
        void deveLancarExcecaoQuandoPacienteNaoExiste() {
            // Arrange
            triagemRequest.setPacienteId(999L);
            when(pacienteService.buscarPorId(999L))
                    .thenThrow(new RecursoNaoEncontradoException("Paciente", 999L));

            // Act & Assert
            assertThatThrownBy(() -> triagemService.realizar(triagemRequest, 1L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }
    }
}
