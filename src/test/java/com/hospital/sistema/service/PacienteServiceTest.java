package com.hospital.sistema.service;

import com.hospital.sistema.dto.PacienteRequest;
import com.hospital.sistema.dto.PacienteResponse;
import com.hospital.sistema.entity.Documento;
import com.hospital.sistema.entity.Endereco;
import com.hospital.sistema.entity.Paciente;
import com.hospital.sistema.enums.TipoDocumento;
import com.hospital.sistema.exception.DocumentoJaCadastradoException;
import com.hospital.sistema.exception.RecursoNaoEncontradoException;
import com.hospital.sistema.exception.ValidacaoException;
import com.hospital.sistema.repository.DocumentoRepository;
import com.hospital.sistema.repository.PacienteRepository;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para PacienteService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PacienteService")
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private DocumentoRepository documentoRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente paciente;
    private PacienteRequest pacienteRequest;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("João Silva");
        paciente.setDocumento(new Documento(TipoDocumento.BILHETE_IDENTIDADE, "123456789AB123"));
        paciente.setEndereco(new Endereco("Rua das Flores, 123"));
        paciente.setTelefone("912345678");
        paciente.setEmail("joao@email.com");
        paciente.setDataNascimento(LocalDate.of(1990, 5, 15));

        pacienteRequest = new PacienteRequest();
        pacienteRequest.setNome("João Silva");
        pacienteRequest.setTipoDocumento("BILHETE_IDENTIDADE");
        pacienteRequest.setNumeroDocumento("123456789AB123");
        pacienteRequest.setEndereco("Rua das Flores, 123");
        pacienteRequest.setTelefone("912345678");
        pacienteRequest.setEmail("joao@email.com");
        pacienteRequest.setDataNascimento("1990-05-15");
    }

    @Nested
    @DisplayName("Listar todos")
    class ListarTodosTests {

        @Test
        @DisplayName("Deve retornar página de pacientes")
        void deveRetornarPaginaDePacientes() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Paciente> pacientes = Arrays.asList(paciente);
            Page<Paciente> page = new PageImpl<>(pacientes, pageable, 1);
            when(pacienteRepository.findAll(pageable)).thenReturn(page);

            // Act
            Page<Paciente> resultado = pacienteService.listarTodos(pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().get(0).getNome()).isEqualTo("João Silva");
            verify(pacienteRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Buscar por ID")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Deve retornar paciente quando ID existe")
        void deveRetornarPacienteQuandoIdExiste() {
            // Arrange
            when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

            // Act
            Paciente resultado = pacienteService.buscarPorId(1L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNome()).isEqualTo("João Silva");
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            // Arrange
            when(pacienteRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> pacienteService.buscarPorId(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }
    }

    @Nested
    @DisplayName("Buscar por nome")
    class BuscarPorNomeTests {

        @Test
        @DisplayName("Deve retornar lista de pacientes pelo nome")
        void deveRetornarListaPorNome() {
            // Arrange
            when(pacienteRepository.buscarPorNome("João")).thenReturn(Arrays.asList(paciente));

            // Act
            List<PacienteResponse> resultado = pacienteService.buscarPorNome("João");

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não encontra pacientes")
        void deveRetornarListaVaziaQuandoNaoEncontra() {
            // Arrange
            when(pacienteRepository.buscarPorNome("XYZ")).thenReturn(List.of());

            // Act
            List<PacienteResponse> resultado = pacienteService.buscarPorNome("XYZ");

            // Assert
            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Salvar paciente")
    class SalvarTests {

        @Test
        @DisplayName("Deve salvar paciente com dados válidos")
        void deveSalvarPacienteComDadosValidos() {
            // Arrange
            when(documentoRepository.existsByNumero(anyString())).thenReturn(false);
            when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

            // Act
            Paciente resultado = pacienteService.salvar(pacienteRequest);

            // Assert
            assertThat(resultado).isNotNull();
            verify(pacienteRepository).save(any(Paciente.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando documento já cadastrado")
        void deveLancarExcecaoQuandoDocumentoJaCadastrado() {
            // Arrange
            when(documentoRepository.existsByNumero("123456789AB123")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> pacienteService.salvar(pacienteRequest))
                    .isInstanceOf(DocumentoJaCadastradoException.class);
            verify(pacienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando formato do documento é inválido")
        void deveLancarExcecaoQuandoFormatoDocumentoInvalido() {
            // Arrange
            pacienteRequest.setNumeroDocumento("INVALIDO");
            when(documentoRepository.existsByNumero(anyString())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> pacienteService.salvar(pacienteRequest))
                    .isInstanceOf(ValidacaoException.class)
                    .hasMessageContaining("documento");
        }

        @Test
        @DisplayName("Deve lançar exceção quando data de nascimento é futura")
        void deveLancarExcecaoQuandoDataFutura() {
            // Arrange
            pacienteRequest.setDataNascimento("2030-01-01");
            when(documentoRepository.existsByNumero(anyString())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> pacienteService.salvar(pacienteRequest))
                    .isInstanceOf(ValidacaoException.class);
        }

        @Test
        @DisplayName("Deve lançar exceção quando telefone tem tamanho inválido")
        void deveLancarExcecaoQuandoTelefoneInvalido() {
            // Arrange
            pacienteRequest.setTelefone("1234"); // Deveria ter 9 dígitos
            when(documentoRepository.existsByNumero(anyString())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> pacienteService.salvar(pacienteRequest))
                    .isInstanceOf(ValidacaoException.class)
                    .hasMessageContaining("telefone");
        }
    }

    @Nested
    @DisplayName("Atualizar paciente")
    class AtualizarTests {

        @Test
        @DisplayName("Deve atualizar paciente com dados válidos")
        void deveAtualizarPacienteComDadosValidos() {
            // Arrange
            when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
            when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

            // Act
            Paciente resultado = pacienteService.atualizar(1L, pacienteRequest);

            // Assert
            assertThat(resultado).isNotNull();
            verify(pacienteRepository).save(any(Paciente.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando paciente não existe")
        void deveLancarExcecaoQuandoPacienteNaoExiste() {
            // Arrange
            when(pacienteRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> pacienteService.atualizar(999L, pacienteRequest))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }
    }

    @Nested
    @DisplayName("Deletar paciente")
    class DeletarTests {

        @Test
        @DisplayName("Deve deletar paciente existente")
        void deveDeletarPacienteExistente() {
            // Arrange
            when(pacienteRepository.existsById(1L)).thenReturn(true);
            doNothing().when(pacienteRepository).deleteById(1L);

            // Act
            pacienteService.deletar(1L);

            // Assert
            verify(pacienteRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando paciente não existe")
        void deveLancarExcecaoQuandoPacienteNaoExiste() {
            // Arrange
            when(pacienteRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> pacienteService.deletar(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
            verify(pacienteRepository, never()).deleteById(anyLong());
        }
    }
}
