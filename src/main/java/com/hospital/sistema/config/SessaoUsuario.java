package com.hospital.sistema.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Gerencia a sessão do usuário logado.
 * Armazena informações como nome, ID e tipo do usuário.
 */
@Component
@SessionScope
public class SessaoUsuario {

    private String nomeUsuario;
    private Long idUsuario;
    private String tipoUsuario;

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    /**
     * Verifica se existe um usuário logado na sessão.
     */
    public boolean isLogado() {
        return nomeUsuario != null && !nomeUsuario.isEmpty();
    }

    /**
     * Verifica se o usuário logado é do tipo especificado.
     */
    public boolean isTipoUsuario(String tipo) {
        return tipoUsuario != null && tipoUsuario.equals(tipo);
    }

    /**
     * Limpa todos os dados da sessão.
     */
    public void limparSessao(HttpServletRequest request) {
        this.nomeUsuario = null;
        this.idUsuario = null;
        this.tipoUsuario = null;

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
