package com.hospital.sistema.controller;

import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.service.ConsultaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recepcao")
public class RecepcionistaController {

    private final SessaoUsuario sessaoUsuario;
    private final ConsultaService consultaService;

    public RecepcionistaController(SessaoUsuario sessaoUsuario, ConsultaService consultaService) {
        this.sessaoUsuario = sessaoUsuario;
        this.consultaService = consultaService;
    }

    @GetMapping
    public String exibirPainelRecepcao(Model model) {
        if (!isRecepcionistaLogado()) {
            return "redirect:/";
        }

        model.addAttribute("title", "Painel da Recepção");
        model.addAttribute("usuario", sessaoUsuario.getNomeUsuario());
        model.addAttribute("consultasHoje", consultaService.buscarConsultasDeHoje());
        return "recepcionista/home";
    }

    private boolean isRecepcionistaLogado() {
        return sessaoUsuario.isLogado() && sessaoUsuario.isTipoUsuario(TipoUsuario.RECEPCIONISTA.getTipo());
    }
}
