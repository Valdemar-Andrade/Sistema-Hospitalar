package com.hospital.sistema.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.dto.LoginRequest;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.exception.CredenciaisInvalidasException;
import com.hospital.sistema.service.AutenticacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes do LoginController usando @WebMvcTest.
 */
@WebMvcTest(LoginController.class)
@DisplayName("LoginController")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AutenticacaoService autenticacaoService;

    @MockBean
    private SessaoUsuario sessaoUsuario;

    @Nested
    @DisplayName("GET /")
    class ExibirPaginaLoginTests {

        @Test
        @DisplayName("Deve exibir página de login")
        void deveExibirPaginaDeLogin() throws Exception {
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("common/index"));
        }
    }

    @Nested
    @DisplayName("POST /login")
    class LoginTests {

        @Test
        @DisplayName("Deve autenticar usuário com credenciais válidas")
        void deveAutenticarUsuarioComCredenciaisValidas() throws Exception {
            // Arrange
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername("admin");
            loginRequest.setPassword("admin123");

            when(autenticacaoService.autenticar("admin", "admin123"))
                    .thenReturn(TipoUsuario.ADMIN.getTipo());
            when(sessaoUsuario.getNomeUsuario()).thenReturn("Administrador");

            // Act & Assert
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem").value("Login bem-sucedido"))
                    .andExpect(jsonPath("$.tipoUsuario").value(TipoUsuario.ADMIN.getTipo()))
                    .andExpect(jsonPath("$.nomeUsuario").value("Administrador"));
        }

        @Test
        @DisplayName("Deve retornar 401 com credenciais inválidas")
        void deveRetornar401ComCredenciaisInvalidas() throws Exception {
            // Arrange
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername("admin");
            loginRequest.setPassword("senhaerrada");

            when(autenticacaoService.autenticar("admin", "senhaerrada"))
                    .thenThrow(new CredenciaisInvalidasException());

            // Act & Assert
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.erro").value("Credenciais inválidas"));
        }

        @Test
        @DisplayName("Deve autenticar médico")
        void deveAutenticarMedico() throws Exception {
            // Arrange
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername("medico");
            loginRequest.setPassword("medico123");

            when(autenticacaoService.autenticar("medico", "medico123"))
                    .thenReturn(TipoUsuario.MEDICO.getTipo());
            when(sessaoUsuario.getNomeUsuario()).thenReturn("Dr. Carlos");

            // Act & Assert
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tipoUsuario").value(TipoUsuario.MEDICO.getTipo()));
        }
    }

    @Nested
    @DisplayName("GET /logout")
    class LogoutTests {

        @Test
        @DisplayName("Deve fazer logout e redirecionar")
        void deveFazerLogoutERedirecionar() throws Exception {
            mockMvc.perform(get("/logout"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attributeExists("message"));
        }
    }
}
