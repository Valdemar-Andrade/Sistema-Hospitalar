package com.hospital.sistema.dto;

public class LoginResponse {

    private String mensagem;
    private String tipoUsuario;
    private String nomeUsuario;

    public LoginResponse() {
    }

    public LoginResponse(String mensagem, String tipoUsuario, String nomeUsuario) {
        this.mensagem = mensagem;
        this.tipoUsuario = tipoUsuario;
        this.nomeUsuario = nomeUsuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
}
