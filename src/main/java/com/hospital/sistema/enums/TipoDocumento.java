package com.hospital.sistema.enums;

public enum TipoDocumento {
    BILHETE_IDENTIDADE("Bilhete de Identidade"),
    PASSAPORTE("Passaporte");

    private final String descricao;

    TipoDocumento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
