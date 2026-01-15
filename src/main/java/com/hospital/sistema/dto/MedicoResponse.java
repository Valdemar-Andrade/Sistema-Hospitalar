package com.hospital.sistema.dto;

import com.hospital.sistema.entity.Especialidade;

public class MedicoResponse {

    private Long id;
    private String nome;
    private String especialidade;
    private String crm;

    public MedicoResponse() {
    }

    public MedicoResponse(Long id, String nome, Especialidade especialidade) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade != null ? especialidade.getNome() : null;
    }

    public MedicoResponse(Long id, String nome, String especialidade, String crm) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.crm = crm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }
}
