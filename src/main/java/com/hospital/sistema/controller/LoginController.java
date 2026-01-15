package com.hospital.sistema.controller;

import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.dto.LoginRequest;
import com.hospital.sistema.dto.LoginResponse;
import com.hospital.sistema.exception.CredenciaisInvalidasException;
import com.hospital.sistema.service.AutenticacaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/")
public class LoginController {

    private final AutenticacaoService autenticacaoService;
    private final SessaoUsuario sessaoUsuario;

    public LoginController(AutenticacaoService autenticacaoService, SessaoUsuario sessaoUsuario) {
        this.autenticacaoService = autenticacaoService;
        this.sessaoUsuario = sessaoUsuario;
    }

    @GetMapping
    public String exibirPaginaLogin() {
        return "common/index";
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        sessaoUsuario.limparSessao(request);
        redirectAttributes.addFlashAttribute("message", "Logout realizado com sucesso.");
        return "redirect:/";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String tipoUsuario = autenticacaoService.autenticar(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            LoginResponse response = new LoginResponse(
                    "Login bem-sucedido",
                    tipoUsuario,
                    sessaoUsuario.getNomeUsuario()
            );

            return ResponseEntity.ok(response);

        } catch (CredenciaisInvalidasException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("erro", "Credenciais inválidas"));
        }
    }
}
