package com.hospital.sistema.util;

import com.hospital.sistema.enums.TipoDocumento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para ValidadorDocumento.
 * Padrão AAA: Arrange, Act, Assert
 */
@DisplayName("ValidadorDocumento")
class ValidadorDocumentoTest {

    @Nested
    @DisplayName("Validação de Bilhete de Identidade")
    class BilheteIdentidadeTests {

        @Test
        @DisplayName("Deve aceitar BI com formato válido (9 dígitos + 2 letras + 3 dígitos)")
        void deveAceitarBIValido() {
            // Arrange
            String biValido = "123456789AB123";
            
            // Act
            boolean resultado = ValidadorDocumento.isValido(TipoDocumento.BILHETE_IDENTIDADE, biValido);
            
            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Deve rejeitar BI com menos dígitos")
        void deveRejeitarBIComMenosDigitos() {
            // Arrange
            String biInvalido = "12345678AB123";
            
            // Act
            boolean resultado = ValidadorDocumento.isValido(TipoDocumento.BILHETE_IDENTIDADE, biInvalido);
            
            // Assert
            assertThat(resultado).isFalse();
        }

        @Test
        @DisplayName("Deve rejeitar BI com letras no lugar de números")
        void deveRejeitarBIComLetrasNoLugarDeNumeros() {
            // Arrange
            String biInvalido = "12345678XAB123";
            
            // Act
            boolean resultado = ValidadorDocumento.isValido(TipoDocumento.BILHETE_IDENTIDADE, biInvalido);
            
            // Assert
            assertThat(resultado).isFalse();
        }

        @Test
        @DisplayName("Deve rejeitar BI com números no lugar das letras")
        void deveRejeitarBIComNumerosNoLugarDasLetras() {
            // Arrange
            String biInvalido = "12345678912123";
            
            // Act
            boolean resultado = ValidadorDocumento.isValido(TipoDocumento.BILHETE_IDENTIDADE, biInvalido);
            
            // Assert
            assertThat(resultado).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {"000000000AA000", "999999999ZZ999", "123456789CD456"})
        @DisplayName("Deve aceitar vários formatos válidos de BI")
        void deveAceitarVariosFormatosValidosBI(String bi) {
            assertThat(ValidadorDocumento.isValido(TipoDocumento.BILHETE_IDENTIDADE, bi)).isTrue();
        }
    }

    @Nested
    @DisplayName("Validação de Passaporte")
    class PassaporteTests {

        @Test
        @DisplayName("Deve aceitar passaporte com qualquer formato não vazio")
        void deveAceitarPassaporteValido() {
            // Arrange
            String passaporteValido = "N123456789";
            
            // Act
            boolean resultado = ValidadorDocumento.isValido(TipoDocumento.PASSAPORTE, passaporteValido);
            
            // Assert
            assertThat(resultado).isTrue();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Deve rejeitar passaporte vazio ou nulo")
        void deveRejeitarPassaporteVazioOuNulo(String passaporte) {
            assertThat(ValidadorDocumento.isValido(TipoDocumento.PASSAPORTE, passaporte)).isFalse();
        }
    }

    @Nested
    @DisplayName("Validações gerais")
    class ValidacoesGeraisTests {

        @Test
        @DisplayName("Deve rejeitar quando tipo de documento é nulo")
        void deveRejeitarTipoDocumentoNulo() {
            // Arrange
            String numero = "123456789AB123";
            
            // Act
            boolean resultado = ValidadorDocumento.isValido(null, numero);
            
            // Assert
            assertThat(resultado).isFalse();
        }

        @Test
        @DisplayName("Deve rejeitar quando número é nulo")
        void deveRejeitarNumeroNulo() {
            assertThat(ValidadorDocumento.isValido(TipoDocumento.BILHETE_IDENTIDADE, null)).isFalse();
        }

        @Test
        @DisplayName("Deve rejeitar quando número está vazio")
        void deveRejeitarNumeroVazio() {
            assertThat(ValidadorDocumento.isValido(TipoDocumento.BILHETE_IDENTIDADE, "")).isFalse();
        }

        @Test
        @DisplayName("Deve rejeitar quando número contém apenas espaços")
        void deveRejeitarNumeroApenasEspacos() {
            assertThat(ValidadorDocumento.isValido(TipoDocumento.BILHETE_IDENTIDADE, "   ")).isFalse();
        }
    }
}
