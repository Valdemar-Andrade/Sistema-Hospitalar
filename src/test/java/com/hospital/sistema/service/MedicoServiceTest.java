package com.hospital.sistema.service;

import com.hospital.sistema.dto.MedicoResponse;
import com.hospital.sistema.entity.Especialidade;
import com.hospital.sistema.entity.Medico;
import com.hospital.sistema.exception.RecursoNaoEncontradoException;
import com.hospital.sistema.repository.MedicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para MedicoService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MedicoService")
class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @InjectMocks
    private MedicoService medicoService;

    private Medico medico;
    private Especialidade especialidade;

    @BeforeEach
    void setUp() {
        especialidade = new Especialidade();
        especialidade.setId(1L);
        especialidade.setNome("Cardiologia");

        medico = new Medico();
        medico.setId(1L);
        medico.setNome("Dr. Carlos Santos");
        medico.setLogin("carlos.santos");
        medico.setSenha("senha123");
        medico.setEspecialidade(especialidade);
    }

    @Nested
    @DisplayName("Listar todos")
    class ListarTodosTests {

        @Test
        @DisplayName("Deve retornar página de médicos")
        void deveRetornarPaginaDeMedicos() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Medico> page = new PageImpl<>(Arrays.asList(medico));
            when(medicoRepository.findAll(pageable)).thenReturn(page);

            // Act
            Page<Medico> resultado = medicoService.listarTodos(pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Dr. Carlos Santos");
        }
    }

    @Nested
    @DisplayName("Buscar por ID")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Deve retornar médico quando ID existe")
        void deveRetornarMedicoQuandoIdExiste() {
            // Arrange
            when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));

            // Act
            Medico resultado = medicoService.buscarPorId(1L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNome()).isEqualTo("Dr. Carlos Santos");
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            // Arrange
            when(medicoRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> medicoService.buscarPorId(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }
    }

    @Nested
    @DisplayName("Buscar por login")
    class BuscarPorLoginTests {

        @Test
        @DisplayName("Deve retornar médico quando login existe")
        void deveRetornarMedicoQuandoLoginExiste() {
            // Arrange
            when(medicoRepository.findByLogin("carlos.santos")).thenReturn(Optional.of(medico));

            // Act
            Optional<Medico> resultado = medicoService.buscarPorLogin("carlos.santos");

            // Assert
            assertThat(resultado).isPresent();
            assertThat(resultado.get().getNome()).isEqualTo("Dr. Carlos Santos");
        }

        @Test
        @DisplayName("Deve retornar empty quando login não existe")
        void deveRetornarEmptyQuandoLoginNaoExiste() {
            // Arrange
            when(medicoRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

            // Act
            Optional<Medico> resultado = medicoService.buscarPorLogin("inexistente");

            // Assert
            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Buscar por nome e especialidade")
    class BuscarPorNomeEEspecialidadeTests {

        @Test
        @DisplayName("Deve retornar lista de médicos filtrada")
        void deveRetornarListaFiltrada() {
            // Arrange
            when(medicoRepository.buscarPorNomeEEspecialidade("Carlos", 1L))
                    .thenReturn(Arrays.asList(medico));

            // Act
            List<MedicoResponse> resultado = medicoService.buscarPorNomeEEspecialidade("Carlos", 1L);

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getNome()).isEqualTo("Dr. Carlos Santos");
        }
    }

    @Nested
    @DisplayName("Salvar médico")
    class SalvarTests {

        @Test
        @DisplayName("Deve salvar médico")
        void deveSalvarMedico() {
            // Arrange
            when(medicoRepository.save(any(Medico.class))).thenReturn(medico);

            // Act
            Medico resultado = medicoService.salvar(medico);

            // Assert
            assertThat(resultado).isNotNull();
            verify(medicoRepository).save(medico);
        }
    }

    @Nested
    @DisplayName("Deletar médico")
    class DeletarTests {

        @Test
        @DisplayName("Deve deletar médico existente")
        void deveDeletarMedicoExistente() {
            // Arrange
            when(medicoRepository.existsById(1L)).thenReturn(true);
            doNothing().when(medicoRepository).deleteById(1L);

            // Act
            medicoService.deletar(1L);

            // Assert
            verify(medicoRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando médico não existe")
        void deveLancarExcecaoQuandoMedicoNaoExiste() {
            // Arrange
            when(medicoRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> medicoService.deletar(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
            verify(medicoRepository, never()).deleteById(anyLong());
        }
    }
}
