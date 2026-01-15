package com.hospital.sistema.enums;

public enum NivelUrgencia {
    EMERGENCIA("Emergência", 1),
    MUITO_URGENTE("Muito Urgente", 2),
    URGENTE("Urgente", 3),
    POUCO_URGENTE("Pouco Urgente", 4),
    NAO_URGENTE("Não Urgente", 5);

    private final String descricao;
    private final int prioridade;

    NivelUrgencia(String descricao, int prioridade) {
        this.descricao = descricao;
        this.prioridade = prioridade;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPrioridade() {
        return prioridade;
    }
}
