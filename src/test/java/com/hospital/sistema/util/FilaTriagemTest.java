package com.hospital.sistema.util;

import com.hospital.sistema.entity.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para FilaTriagem.
 */
@DisplayName("FilaTriagem")
class FilaTriagemTest {

    private FilaTriagem filaTriagem;

    @BeforeEach
    void setUp() {
        filaTriagem = new FilaTriagem();
    }

    private Paciente criarPaciente(Long id, String nome) {
        Paciente paciente = new Paciente();
        paciente.setId(id);
        paciente.setNome(nome);
        return paciente;
    }

    @Nested
    @DisplayName("Adicionar paciente")
    class AdicionarTests {

        @Test
        @DisplayName("Deve adicionar paciente na fila")
        void deveAdicionarPaciente() {
            // Arrange
            Paciente paciente = criarPaciente(1L, "João Silva");

            // Act
            filaTriagem.adicionar(paciente);

            // Assert
            assertThat(filaTriagem.tamanhoFila()).isEqualTo(1);
            assertThat(filaTriagem.contemPaciente(1L)).isTrue();
        }

        @Test
        @DisplayName("Não deve adicionar paciente nulo")
        void naoDeveAdicionarPacienteNulo() {
            // Act
            filaTriagem.adicionar(null);

            // Assert
            assertThat(filaTriagem.tamanhoFila()).isZero();
        }

        @Test
        @DisplayName("Não deve adicionar paciente duplicado")
        void naoDeveAdicionarPacienteDuplicado() {
            // Arrange
            Paciente paciente = criarPaciente(1L, "João Silva");

            // Act
            filaTriagem.adicionar(paciente);
            filaTriagem.adicionar(paciente);

            // Assert
            assertThat(filaTriagem.tamanhoFila()).isEqualTo(1);
        }

        @Test
        @DisplayName("Deve manter ordem de inserção")
        void deveManterOrdemDeInsercao() {
            // Arrange
            Paciente paciente1 = criarPaciente(1L, "Primeiro");
            Paciente paciente2 = criarPaciente(2L, "Segundo");
            Paciente paciente3 = criarPaciente(3L, "Terceiro");

            // Act
            filaTriagem.adicionar(paciente1);
            filaTriagem.adicionar(paciente2);
            filaTriagem.adicionar(paciente3);

            // Assert
            assertThat(filaTriagem.listarPacientes())
                    .extracting(Paciente::getNome)
                    .containsExactly("Primeiro", "Segundo", "Terceiro");
        }
    }

    @Nested
    @DisplayName("Remover paciente")
    class RemoverTests {

        @Test
        @DisplayName("Deve remover paciente da fila")
        void deveRemoverPaciente() {
            // Arrange
            Paciente paciente = criarPaciente(1L, "João Silva");
            filaTriagem.adicionar(paciente);

            // Act
            filaTriagem.remover(1L);

            // Assert
            assertThat(filaTriagem.tamanhoFila()).isZero();
            assertThat(filaTriagem.contemPaciente(1L)).isFalse();
        }

        @Test
        @DisplayName("Deve ignorar remoção de paciente inexistente")
        void deveIgnorarRemocaoDeInexistente() {
            // Arrange
            Paciente paciente = criarPaciente(1L, "João Silva");
            filaTriagem.adicionar(paciente);

            // Act
            filaTriagem.remover(999L);

            // Assert
            assertThat(filaTriagem.tamanhoFila()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Buscar próximo")
    class BuscarProximoTests {

        @Test
        @DisplayName("Deve retornar primeiro paciente da fila")
        void deveRetornarPrimeiroPaciente() {
            // Arrange
            Paciente paciente1 = criarPaciente(1L, "Primeiro");
            Paciente paciente2 = criarPaciente(2L, "Segundo");
            filaTriagem.adicionar(paciente1);
            filaTriagem.adicionar(paciente2);

            // Act
            Optional<Paciente> proximo = filaTriagem.buscarProximo();

            // Assert
            assertThat(proximo).isPresent();
            assertThat(proximo.get().getNome()).isEqualTo("Primeiro");
        }

        @Test
        @DisplayName("Deve retornar empty quando fila está vazia")
        void deveRetornarEmptyQuandoFilaVazia() {
            // Act
            Optional<Paciente> proximo = filaTriagem.buscarProximo();

            // Assert
            assertThat(proximo).isEmpty();
        }
    }

    @Nested
    @DisplayName("Listar pacientes")
    class ListarPacientesTests {

        @Test
        @DisplayName("Deve retornar cópia da lista")
        void deveRetornarCopiaDaLista() {
            // Arrange
            Paciente paciente = criarPaciente(1L, "João");
            filaTriagem.adicionar(paciente);

            // Act
            var lista = filaTriagem.listarPacientes();
            lista.clear(); // Limpa a lista retornada

            // Assert - A lista original não deve ser afetada
            assertThat(filaTriagem.tamanhoFila()).isEqualTo(1);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando fila está vazia")
        void deveRetornarListaVazia() {
            assertThat(filaTriagem.listarPacientes()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Contém paciente")
    class ContemPacienteTests {

        @Test
        @DisplayName("Deve retornar true quando paciente existe")
        void deveRetornarTrueQuandoExiste() {
            // Arrange
            Paciente paciente = criarPaciente(1L, "João");
            filaTriagem.adicionar(paciente);

            // Assert
            assertThat(filaTriagem.contemPaciente(1L)).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando paciente não existe")
        void deveRetornarFalseQuandoNaoExiste() {
            assertThat(filaTriagem.contemPaciente(999L)).isFalse();
        }
    }
}
