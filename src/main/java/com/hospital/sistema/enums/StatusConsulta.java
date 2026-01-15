package com.hospital.sistema.enums;

public enum StatusConsulta {
    AGENDADA("Agendada"),
    EM_TRIAGEM("Em Triagem"),
    AGUARDANDO_ATENDIMENTO("Aguardando Atendimento"),
    EM_ATENDIMENTO("Em Atendimento"),
    REALIZADA("Realizada"),
    CANCELADA("Cancelada"),
    NAO_COMPARECEU("Não Compareceu");

    private final String descricao;

    StatusConsulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
