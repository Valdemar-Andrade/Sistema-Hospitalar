package com.hospital.sistema.controller;

import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.dto.TriagemRequest;
import com.hospital.sistema.entity.Paciente;
import com.hospital.sistema.entity.Triagem;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.service.TriagemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/enfermeiro/triagem")
public class TriagemController {

    private final TriagemService triagemService;
    private final SessaoUsuario sessaoUsuario;

    public TriagemController(TriagemService triagemService, SessaoUsuario sessaoUsuario) {
        this.triagemService = triagemService;
        this.sessaoUsuario = sessaoUsuario;
    }

    @GetMapping
    public String exibirPainelTriagem(Model model) {
        if (!isEnfermeiroLogado()) {
            return "redirect:/";
        }

        model.addAttribute("title", "Painel de Triagem");
        model.addAttribute("usuario", sessaoUsuario.getNomeUsuario());
        return "enfermeiro/triagem";
    }

    @GetMapping("/fila")
    @ResponseBody
    public ResponseEntity<List<Paciente>> listarFilaTriagem() {
        if (!isEnfermeiroLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Paciente> fila = triagemService.listarFilaTriagem();
        return ResponseEntity.ok(fila);
    }

    @PostMapping("/realizar")
    @ResponseBody
    public ResponseEntity<?> realizarTriagem(@Valid @RequestBody TriagemRequest request) {
        if (!isEnfermeiroLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Triagem triagem = triagemService.realizar(request, sessaoUsuario.getIdUsuario());
        return ResponseEntity.ok(Map.of(
                "mensagem", "Triagem realizada com sucesso",
                "triagemId", triagem.getId()
        ));
    }

    private boolean isEnfermeiroLogado() {
        return sessaoUsuario.isLogado() && sessaoUsuario.isTipoUsuario(TipoUsuario.ENFERMEIRO.getTipo());
    }
}
