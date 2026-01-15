package com.hospital.sistema.controller;

import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final SessaoUsuario sessaoUsuario;
    private final AdminService adminService;

    public AdminController(SessaoUsuario sessaoUsuario, AdminService adminService) {
        this.sessaoUsuario = sessaoUsuario;
        this.adminService = adminService;
    }

    @GetMapping
    public String exibirPainelAdmin(Model model) {
        if (!isAdminLogado()) {
            return "redirect:/";
        }

        model.addAttribute("title", "Painel Administrativo");
        model.addAttribute("usuario", sessaoUsuario.getNomeUsuario());
        return "admin/home";
    }

    @GetMapping("/configuracoes")
    public String exibirConfiguracoes(Model model) {
        if (!isAdminLogado()) {
            return "redirect:/";
        }

        model.addAttribute("title", "Configurações");
        return "admin/configuracoes";
    }

    @PostMapping("/atualizar-credenciais")
    @ResponseBody
    public ResponseEntity<?> atualizarCredenciais(@RequestBody Map<String, String> dados) {
        if (!isAdminLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String login = dados.get("login");
        String senha = dados.get("senha");

        return adminService.atualizarCredenciais(sessaoUsuario.getIdUsuario(), login, senha)
                .map(admin -> ResponseEntity.ok(Map.of("mensagem", "Credenciais atualizadas com sucesso")))
                .orElse(ResponseEntity.badRequest().body(Map.of("erro", "Erro ao atualizar credenciais")));
    }

    private boolean isAdminLogado() {
        return sessaoUsuario.isLogado() && sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo());
    }
}
