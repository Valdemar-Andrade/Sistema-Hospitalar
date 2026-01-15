package com.hospital.sistema.enums;

public enum TipoUsuario {
    ADMIN("ADMIN"),
    MEDICO("MEDICO"),
    ENFERMEIRO("ENFERMEIRO"),
    RECEPCIONISTA("RECEPCIONISTA");

    private final String tipo;

    TipoUsuario(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
