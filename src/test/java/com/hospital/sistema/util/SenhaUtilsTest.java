package com.hospital.sistema.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para SenhaUtils.
 */
@DisplayName("SenhaUtils")
class SenhaUtilsTest {

    @Nested
    @DisplayName("Método criptografar")
    class CriptografarTests {

        @Test
        @DisplayName("Deve criptografar senha e retornar string não vazia")
        void deveCriptografarSenha() {
            // Arrange
            String senhaOriginal = "minhasenha123";
            
            // Act
            String senhaCriptografada = SenhaUtils.criptografar(senhaOriginal);
            
            // Assert
            assertThat(senhaCriptografada)
                    .isNotNull()
                    .isNotEmpty()
                    .isNotEqualTo(senhaOriginal);
        }

        @Test
        @DisplayName("Deve gerar hash consistente para mesma senha")
        void deveGerarHashConsistente() {
            // Arrange
            String senha = "teste123";
            
            // Act
            String hash1 = SenhaUtils.criptografar(senha);
            String hash2 = SenhaUtils.criptografar(senha);
            
            // Assert
            assertThat(hash1).isEqualTo(hash2);
        }

        @Test
        @DisplayName("Deve gerar hashes diferentes para senhas diferentes")
        void deveGerarHashesDiferentes() {
            // Arrange
            String senha1 = "senha123";
            String senha2 = "senha456";
            
            // Act
            String hash1 = SenhaUtils.criptografar(senha1);
            String hash2 = SenhaUtils.criptografar(senha2);
            
            // Assert
            assertThat(hash1).isNotEqualTo(hash2);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "abc", "senha muito longa com vários caracteres especiais!@#$%"})
        @DisplayName("Deve criptografar senhas de diferentes tamanhos")
        void deveCriptografarSenhasDeDiferentesTamanhos(String senha) {
            String hash = SenhaUtils.criptografar(senha);
            assertThat(hash).isNotNull().isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Método verificar")
    class VerificarTests {

        @Test
        @DisplayName("Deve retornar true quando senha está correta")
        void deveRetornarTrueQuandoSenhaCorreta() {
            // Arrange
            String senhaOriginal = "minhasenha";
            String senhaCriptografada = SenhaUtils.criptografar(senhaOriginal);
            
            // Act
            boolean resultado = SenhaUtils.verificar(senhaOriginal, senhaCriptografada);
            
            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando senha está incorreta")
        void deveRetornarFalseQuandoSenhaIncorreta() {
            // Arrange
            String senhaOriginal = "minhasenha";
            String senhaCriptografada = SenhaUtils.criptografar(senhaOriginal);
            
            // Act
            boolean resultado = SenhaUtils.verificar("senhaerrada", senhaCriptografada);
            
            // Assert
            assertThat(resultado).isFalse();
        }

        @Test
        @DisplayName("Deve ser case sensitive")
        void deveSerCaseSensitive() {
            // Arrange
            String senhaCriptografada = SenhaUtils.criptografar("Senha");
            
            // Act & Assert
            assertThat(SenhaUtils.verificar("Senha", senhaCriptografada)).isTrue();
            assertThat(SenhaUtils.verificar("senha", senhaCriptografada)).isFalse();
            assertThat(SenhaUtils.verificar("SENHA", senhaCriptografada)).isFalse();
        }

        @Test
        @DisplayName("Deve verificar senhas com caracteres especiais")
        void deveVerificarSenhaComCaracteresEspeciais() {
            // Arrange
            String senhaEspecial = "p@ss#w0rd!$%";
            String hash = SenhaUtils.criptografar(senhaEspecial);
            
            // Act & Assert
            assertThat(SenhaUtils.verificar(senhaEspecial, hash)).isTrue();
        }
    }
}
