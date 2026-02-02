package com.hospital.sistema;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de carga do contexto Spring Boot.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Sistema Hospitalar Application")
class SistemaHospitalarApplicationTests {

    @Test
    @DisplayName("Contexto da aplicação deve carregar com sucesso")
    void contextLoads() {
        // Se chegou aqui, o contexto carregou corretamente
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Aplicação deve iniciar sem erros")
    void applicationStarts() {
        SistemaHospitalarApplication.main(new String[]{});
    }
}
