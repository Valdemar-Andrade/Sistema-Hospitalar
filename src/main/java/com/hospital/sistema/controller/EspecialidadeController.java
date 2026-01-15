package com.hospital.sistema.controller;

import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.entity.Especialidade;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.service.EspecialidadeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/especialidades")
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;
    private final SessaoUsuario sessaoUsuario;

    public EspecialidadeController(EspecialidadeService especialidadeService, SessaoUsuario sessaoUsuario) {
        this.especialidadeService = especialidadeService;
        this.sessaoUsuario = sessaoUsuario;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Especialidade>> listarTodas() {
        if (!sessaoUsuario.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(especialidadeService.listarTodas());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> salvar(@Valid @RequestBody Especialidade especialidade) {
        if (!isAdminLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Especialidade salva = especialidadeService.salvar(especialidade);
        return ResponseEntity.ok(salva);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!isAdminLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        especialidadeService.deletar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Especialidade excluída com sucesso"));
    }

    private boolean isAdminLogado() {
        return sessaoUsuario.isLogado() && sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo());
    }
}
