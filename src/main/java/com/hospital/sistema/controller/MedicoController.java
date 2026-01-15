package com.hospital.sistema.controller;

import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.dto.MedicoResponse;
import com.hospital.sistema.dto.PacienteFilaDTO;
import com.hospital.sistema.entity.Medico;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.service.MedicoService;
import com.hospital.sistema.util.FilaAtendimentoMedico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/medico")
public class MedicoController {

    private final MedicoService medicoService;
    private final SessaoUsuario sessaoUsuario;
    private final FilaAtendimentoMedico filaAtendimentoMedico;

    public MedicoController(MedicoService medicoService,
                           SessaoUsuario sessaoUsuario,
                           FilaAtendimentoMedico filaAtendimentoMedico) {
        this.medicoService = medicoService;
        this.sessaoUsuario = sessaoUsuario;
        this.filaAtendimentoMedico = filaAtendimentoMedico;
    }

    @GetMapping
    public String exibirPainelMedico(Model model) {
        if (!isMedicoLogado()) {
            return "redirect:/";
        }

        model.addAttribute("title", "Painel Médico");
        model.addAttribute("usuario", sessaoUsuario.getNomeUsuario());
        return "medico/home";
    }

    @GetMapping("/fila")
    @ResponseBody
    public ResponseEntity<List<PacienteFilaDTO>> listarFilaAtendimento() {
        if (!isMedicoLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PacienteFilaDTO> fila = filaAtendimentoMedico.listarPorMedico(sessaoUsuario.getIdUsuario());
        return ResponseEntity.ok(fila);
    }

    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<List<MedicoResponse>> buscarMedicos(
            @RequestParam String term,
            @RequestParam(required = false) Long especialidadeId) {

        if (!sessaoUsuario.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<MedicoResponse> medicos = medicoService.buscarPorNomeEEspecialidade(term, especialidadeId);
        return ResponseEntity.ok(medicos);
    }

    private boolean isMedicoLogado() {
        return sessaoUsuario.isLogado() && sessaoUsuario.isTipoUsuario(TipoUsuario.MEDICO.getTipo());
    }
}
