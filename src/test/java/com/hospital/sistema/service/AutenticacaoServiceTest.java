package com.hospital.sistema.service;

import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.entity.Admin;
import com.hospital.sistema.entity.Enfermeiro;
import com.hospital.sistema.entity.Medico;
import com.hospital.sistema.entity.Recepcionista;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.exception.CredenciaisInvalidasException;
import com.hospital.sistema.repository.*;
import com.hospital.sistema.util.SenhaUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AutenticacaoService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AutenticacaoService")
class AutenticacaoServiceTest {

    @Mock
    private SessaoUsuario sessaoUsuario;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private EnfermeiroRepository enfermeiroRepository;

    @Mock
    private RecepcionistaRepository recepcionistaRepository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Nested
    @DisplayName("Autenticação de Admin")
    class AdminTests {

        @Test
        @DisplayName("Deve autenticar admin com credenciais válidas")
        void deveAutenticarAdminComCredenciaisValidas() {
            // Arrange
            Admin admin = new Admin();
            admin.setId(1L);
            admin.setNome("Administrador");
            admin.setLogin("admin");
            admin.setSenha(SenhaUtils.criptografar("admin123"));

            when(adminRepository.findByLogin("admin")).thenReturn(Optional.of(admin));

            // Act
            String resultado = autenticacaoService.autenticar("admin", "admin123");

            // Assert
            assertThat(resultado).isEqualTo(TipoUsuario.ADMIN.getTipo());
            verify(sessaoUsuario).setNomeUsuario("Administrador");
            verify(sessaoUsuario).setIdUsuario(1L);
        }

        @Test
        @DisplayName("Deve autenticar admin com senha em texto plano")
        void deveAutenticarAdminComSenhaTextoPlano() {
            // Arrange
            Admin admin = new Admin();
            admin.setId(1L);
            admin.setNome("Administrador");
            admin.setLogin("admin");
            admin.setSenha("admin123"); // Senha não criptografada

            when(adminRepository.findByLogin("admin")).thenReturn(Optional.of(admin));

            // Act
            String resultado = autenticacaoService.autenticar("admin", "admin123");

            // Assert
            assertThat(resultado).isEqualTo(TipoUsuario.ADMIN.getTipo());
        }
    }

    @Nested
    @DisplayName("Autenticação de Médico")
    class MedicoTests {

        @Test
        @DisplayName("Deve autenticar médico com credenciais válidas")
        void deveAutenticarMedicoComCredenciaisValidas() {
            // Arrange
            Medico medico = new Medico();
            medico.setId(1L);
            medico.setNome("Dr. Carlos");
            medico.setLogin("carlos");
            medico.setSenha(SenhaUtils.criptografar("medico123"));

            when(adminRepository.findByLogin("carlos")).thenReturn(Optional.empty());
            when(medicoRepository.findByLogin("carlos")).thenReturn(Optional.of(medico));

            // Act
            String resultado = autenticacaoService.autenticar("carlos", "medico123");

            // Assert
            assertThat(resultado).isEqualTo(TipoUsuario.MEDICO.getTipo());
        }
    }

    @Nested
    @DisplayName("Autenticação de Enfermeiro")
    class EnfermeiroTests {

        @Test
        @DisplayName("Deve autenticar enfermeiro com credenciais válidas")
        void deveAutenticarEnfermeiroComCredenciaisValidas() {
            // Arrange
            Enfermeiro enfermeiro = new Enfermeiro();
            enfermeiro.setId(1L);
            enfermeiro.setNome("Enf. Ana");
            enfermeiro.setLogin("ana");
            enfermeiro.setSenha(SenhaUtils.criptografar("enf123"));

            when(adminRepository.findByLogin("ana")).thenReturn(Optional.empty());
            when(medicoRepository.findByLogin("ana")).thenReturn(Optional.empty());
            when(enfermeiroRepository.findByLogin("ana")).thenReturn(Optional.of(enfermeiro));

            // Act
            String resultado = autenticacaoService.autenticar("ana", "enf123");

            // Assert
            assertThat(resultado).isEqualTo(TipoUsuario.ENFERMEIRO.getTipo());
        }
    }

    @Nested
    @DisplayName("Autenticação de Recepcionista")
    class RecepcionistaTests {

        @Test
        @DisplayName("Deve autenticar recepcionista com credenciais válidas")
        void deveAutenticarRecepcionistaComCredenciaisValidas() {
            // Arrange
            Recepcionista recepcionista = new Recepcionista();
            recepcionista.setId(1L);
            recepcionista.setNome("Maria");
            recepcionista.setLogin("maria");
            recepcionista.setSenha(SenhaUtils.criptografar("recep123"));

            when(adminRepository.findByLogin("maria")).thenReturn(Optional.empty());
            when(medicoRepository.findByLogin("maria")).thenReturn(Optional.empty());
            when(enfermeiroRepository.findByLogin("maria")).thenReturn(Optional.empty());
            when(recepcionistaRepository.findByLogin("maria")).thenReturn(Optional.of(recepcionista));

            // Act
            String resultado = autenticacaoService.autenticar("maria", "recep123");

            // Assert
            assertThat(resultado).isEqualTo(TipoUsuario.RECEPCIONISTA.getTipo());
        }
    }

    @Nested
    @DisplayName("Credenciais inválidas")
    class CredenciaisInvalidasTests {

        @Test
        @DisplayName("Deve lançar exceção quando login não existe")
        void deveLancarExcecaoQuandoLoginNaoExiste() {
            // Arrange
            when(adminRepository.findByLogin("inexistente")).thenReturn(Optional.empty());
            when(medicoRepository.findByLogin("inexistente")).thenReturn(Optional.empty());
            when(enfermeiroRepository.findByLogin("inexistente")).thenReturn(Optional.empty());
            when(recepcionistaRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> autenticacaoService.autenticar("inexistente", "senha"))
                    .isInstanceOf(CredenciaisInvalidasException.class);
        }

        @Test
        @DisplayName("Deve lançar exceção quando senha está incorreta")
        void deveLancarExcecaoQuandoSenhaIncorreta() {
            // Arrange
            Admin admin = new Admin();
            admin.setId(1L);
            admin.setNome("Admin");
            admin.setLogin("admin");
            admin.setSenha(SenhaUtils.criptografar("senhaCorreta"));

            when(adminRepository.findByLogin("admin")).thenReturn(Optional.of(admin));
            when(medicoRepository.findByLogin("admin")).thenReturn(Optional.empty());
            when(enfermeiroRepository.findByLogin("admin")).thenReturn(Optional.empty());
            when(recepcionistaRepository.findByLogin("admin")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> autenticacaoService.autenticar("admin", "senhaErrada"))
                    .isInstanceOf(CredenciaisInvalidasException.class);
        }
    }
}
