package com.hospital.sistema.controller;

import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.dto.ConsultaRequest;
import com.hospital.sistema.dto.ConsultaResponse;
import com.hospital.sistema.entity.Consulta;
import com.hospital.sistema.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final SessaoUsuario sessaoUsuario;

    public ConsultaController(ConsultaService consultaService, SessaoUsuario sessaoUsuario) {
        this.consultaService = consultaService;
        this.sessaoUsuario = sessaoUsuario;
    }

    @GetMapping("/hoje")
    @ResponseBody
    public ResponseEntity<List<ConsultaResponse>> listarConsultasDeHoje() {
        if (!sessaoUsuario.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ConsultaResponse> consultas = consultaService.buscarConsultasDeHoje();
        return ResponseEntity.ok(consultas);
    }

    @PostMapping("/agendar")
    @ResponseBody
    public ResponseEntity<?> agendarConsulta(@Valid @RequestBody ConsultaRequest request) {
        if (!sessaoUsuario.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return consultaService.agendar(request)
                .map(consulta -> ResponseEntity.ok(Map.of(
                        "mensagem", "Consulta agendada com sucesso",
                        "consultaId", consulta.getId()
                )))
                .orElse(ResponseEntity.badRequest().body(Map.of(
                        "erro", "Não foi possível agendar a consulta"
                )));
    }

    @PostMapping("/{id}/encaminhar-triagem")
    @ResponseBody
    public ResponseEntity<?> encaminharParaTriagem(@PathVariable Long id) {
        if (!sessaoUsuario.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean sucesso = consultaService.encaminharParaTriagem(id);

        if (sucesso) {
            return ResponseEntity.ok(Map.of("mensagem", "Paciente encaminhado para triagem"));
        }
        return ResponseEntity.badRequest().body(Map.of("erro", "Consulta não encontrada"));
    }

    @GetMapping("/paciente/{pacienteId}/historico")
    @ResponseBody
    public ResponseEntity<List<Consulta>> buscarHistoricoPaciente(@PathVariable Long pacienteId) {
        if (!sessaoUsuario.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Consulta> historico = consultaService.buscarHistoricoPaciente(pacienteId);
        return ResponseEntity.ok(historico);
    }
}
