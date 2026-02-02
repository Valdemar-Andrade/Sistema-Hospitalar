package com.hospital.sistema.repository;

import com.hospital.sistema.entity.Documento;
import com.hospital.sistema.entity.Paciente;
import com.hospital.sistema.enums.TipoDocumento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para PacienteRepository usando @DataJpaTest.
 * Utiliza banco H2 em memória.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("PacienteRepository")
class PacienteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PacienteRepository pacienteRepository;

    private Paciente paciente1;
    private Paciente paciente2;

    @BeforeEach
    void setUp() {
        // Criar pacientes de teste
        paciente1 = new Paciente();
        paciente1.setNome("João Silva");
        paciente1.setDocumento(new Documento(TipoDocumento.BILHETE_IDENTIDADE, "123456789AB123"));
        paciente1.setTelefone("912345678");
        paciente1.setEmail("joao@email.com");
        paciente1.setDataNascimento(LocalDate.of(1990, 5, 15));

        paciente2 = new Paciente();
        paciente2.setNome("Maria Santos");
        paciente2.setDocumento(new Documento(TipoDocumento.BILHETE_IDENTIDADE, "987654321CD456"));
        paciente2.setTelefone("923456789");
        paciente2.setEmail("maria@email.com");
        paciente2.setDataNascimento(LocalDate.of(1985, 8, 20));
    }

    @Nested
    @DisplayName("findAll com paginação")
    class FindAllPaginadoTests {

        @Test
        @DisplayName("Deve retornar página de pacientes")
        void deveRetornarPaginaDePacientes() {
            // Arrange
            entityManager.persist(paciente1);
            entityManager.persist(paciente2);
            entityManager.flush();

            // Act
            Page<Paciente> page = pacienteRepository.findAll(PageRequest.of(0, 10));

            // Assert
            assertThat(page.getContent()).hasSize(2);
            assertThat(page.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há pacientes")
        void deveRetornarPaginaVazia() {
            // Act
            Page<Paciente> page = pacienteRepository.findAll(PageRequest.of(0, 10));

            // Assert
            assertThat(page.getContent()).isEmpty();
            assertThat(page.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("buscarPorNome")
    class BuscarPorNomeTests {

        @Test
        @DisplayName("Deve encontrar pacientes pelo nome parcial")
        void deveEncontrarPacientesPeloNomeParcial() {
            // Arrange
            entityManager.persist(paciente1);
            entityManager.persist(paciente2);
            entityManager.flush();

            // Act
            List<Paciente> resultado = pacienteRepository.buscarPorNome("João");

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");
        }

        @Test
        @DisplayName("Deve buscar ignorando case")
        void deveBuscarIgnorandoCase() {
            // Arrange
            entityManager.persist(paciente1);
            entityManager.flush();

            // Act
            List<Paciente> resultadoMinusculo = pacienteRepository.buscarPorNome("joão");
            List<Paciente> resultadoMaiusculo = pacienteRepository.buscarPorNome("JOÃO");

            // Assert
            assertThat(resultadoMinusculo).hasSize(1);
            assertThat(resultadoMaiusculo).hasSize(1);
        }

        @Test
        @DisplayName("Deve encontrar vários pacientes com nome similar")
        void deveEncontrarVariosPacientesComNomeSimilar() {
            // Arrange
            Paciente paciente3 = new Paciente();
            paciente3.setNome("João Pedro");
            paciente3.setDocumento(new Documento(TipoDocumento.PASSAPORTE, "N12345678"));
            paciente3.setDataNascimento(LocalDate.of(1995, 1, 1));

            entityManager.persist(paciente1);
            entityManager.persist(paciente3);
            entityManager.flush();

            // Act
            List<Paciente> resultado = pacienteRepository.buscarPorNome("João");

            // Assert
            assertThat(resultado).hasSize(2);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não encontra")
        void deveRetornarListaVaziaQuandoNaoEncontra() {
            // Arrange
            entityManager.persist(paciente1);
            entityManager.flush();

            // Act
            List<Paciente> resultado = pacienteRepository.buscarPorNome("XYZ");

            // Assert
            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("buscarPorDocumento")
    class BuscarPorDocumentoTests {

        @Test
        @DisplayName("Deve encontrar paciente pelo número do documento")
        void deveEncontrarPacientePeloDocumento() {
            // Arrange
            entityManager.persist(paciente1);
            entityManager.flush();

            // Act
            Paciente resultado = pacienteRepository.buscarPorDocumento("123456789AB123");

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNome()).isEqualTo("João Silva");
        }

        @Test
        @DisplayName("Deve retornar null quando documento não existe")
        void deveRetornarNullQuandoDocumentoNaoExiste() {
            // Act
            Paciente resultado = pacienteRepository.buscarPorDocumento("INEXISTENTE");

            // Assert
            assertThat(resultado).isNull();
        }
    }

    @Nested
    @DisplayName("Operações CRUD")
    class CrudTests {

        @Test
        @DisplayName("Deve salvar paciente com documento")
        void deveSalvarPacienteComDocumento() {
            // Act
            Paciente salvo = pacienteRepository.save(paciente1);

            // Assert
            assertThat(salvo.getId()).isNotNull();
            assertThat(salvo.getDocumento()).isNotNull();
            assertThat(salvo.getDocumento().getNumero()).isEqualTo("123456789AB123");
        }

        @Test
        @DisplayName("Deve atualizar paciente")
        void deveAtualizarPaciente() {
            // Arrange
            entityManager.persist(paciente1);
            entityManager.flush();

            // Act
            paciente1.setNome("João Silva Atualizado");
            Paciente atualizado = pacienteRepository.save(paciente1);

            // Assert
            assertThat(atualizado.getNome()).isEqualTo("João Silva Atualizado");
        }

        @Test
        @DisplayName("Deve deletar paciente")
        void deveDeletarPaciente() {
            // Arrange
            entityManager.persist(paciente1);
            entityManager.flush();
            Long id = paciente1.getId();

            // Act
            pacienteRepository.deleteById(id);

            // Assert
            assertThat(pacienteRepository.findById(id)).isEmpty();
        }

        @Test
        @DisplayName("Deve verificar existência por ID")
        void deveVerificarExistenciaPorId() {
            // Arrange
            entityManager.persist(paciente1);
            entityManager.flush();

            // Assert
            assertThat(pacienteRepository.existsById(paciente1.getId())).isTrue();
            assertThat(pacienteRepository.existsById(999L)).isFalse();
        }
    }
}
