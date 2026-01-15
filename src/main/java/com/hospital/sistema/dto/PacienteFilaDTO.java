package com.hospital.sistema.dto;

/**
 * DTO para representar um paciente na fila de atendimento.
 */
public class PacienteFilaDTO {

    private Long id;
    private String nome;
    private Long medicoId;
    private Long consultaId;
    private String nivelUrgencia;

    public PacienteFilaDTO() {
    }

    public PacienteFilaDTO(Long id, String nome, Long medicoId, Long consultaId, String nivelUrgencia) {
        this.id = id;
        this.nome = nome;
        this.medicoId = medicoId;
        this.consultaId = consultaId;
        this.nivelUrgencia = nivelUrgencia;
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

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public Long getConsultaId() {
        return consultaId;
    }

    public void setConsultaId(Long consultaId) {
        this.consultaId = consultaId;
    }

    public String getNivelUrgencia() {
        return nivelUrgencia;
    }

    public void setNivelUrgencia(String nivelUrgencia) {
        this.nivelUrgencia = nivelUrgencia;
    }
}
