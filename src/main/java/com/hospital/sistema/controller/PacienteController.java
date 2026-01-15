package com.hospital.sistema.controller;

import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.dto.PacienteRequest;
import com.hospital.sistema.dto.PacienteResponse;
import com.hospital.sistema.entity.Paciente;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;
    private final SessaoUsuario sessaoUsuario;

    public PacienteController(PacienteService pacienteService, SessaoUsuario sessaoUsuario) {
        this.pacienteService = pacienteService;
        this.sessaoUsuario = sessaoUsuario;
    }

    @GetMapping
    public String listarPacientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "60") int size,
            @RequestParam(defaultValue = "nome") String sort,
            Model model) {

        if (!isUsuarioAutorizado(TipoUsuario.ADMIN)) {
            return "redirect:/";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Paciente> pacientesPage = pacienteService.listarTodos(pageable);

        adicionarAtributosPaginacao(model, pacientesPage, page, size, sort);
        return "admin/pacientes/pacientes";
    }

    @PostMapping
    public ResponseEntity<?> salvarPaciente(@Valid @RequestBody PacienteRequest request) {
        if (!isUsuarioAutorizadoParaGerenciarPaciente()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Paciente paciente = pacienteService.salvar(request);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("/{id}/editar")
    @ResponseBody
    public ResponseEntity<?> buscarPacienteParaEdicao(@PathVariable Long id) {
        if (!sessaoUsuario.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Paciente paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping(value = "/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<PacienteResponse>> buscarPacientesPorNome(@RequestParam String term) {
        if (!sessaoUsuario.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PacienteResponse> pacientes = pacienteService.buscarPorNome(term);
        return ResponseEntity.ok(pacientes);
    }

    @PutMapping("/{id}/atualizar")
    public ResponseEntity<?> atualizarPaciente(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequest request) {

        if (!isUsuarioAutorizado(TipoUsuario.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Acesso não autorizado"));
        }

        Paciente paciente = pacienteService.atualizar(id, request);
        return ResponseEntity.ok(paciente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirPaciente(@PathVariable Long id) {
        if (!isUsuarioAutorizado(TipoUsuario.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        pacienteService.deletar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Paciente excluído com sucesso"));
    }

    private boolean isUsuarioAutorizado(TipoUsuario tipo) {
        return sessaoUsuario.isLogado() && sessaoUsuario.isTipoUsuario(tipo.getTipo());
    }

    private boolean isUsuarioAutorizadoParaGerenciarPaciente() {
        return sessaoUsuario.isLogado() &&
                (sessaoUsuario.isTipoUsuario(TipoUsuario.ADMIN.getTipo()) ||
                 sessaoUsuario.isTipoUsuario(TipoUsuario.RECEPCIONISTA.getTipo()));
    }

    private void adicionarAtributosPaginacao(Model model, Page<Paciente> page, int pageNum, int size, String sort) {
        model.addAttribute("pacientes", page.getContent());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("first", page.isFirst());
        model.addAttribute("last", page.isLast());
        model.addAttribute("sort", sort);
    }
}
