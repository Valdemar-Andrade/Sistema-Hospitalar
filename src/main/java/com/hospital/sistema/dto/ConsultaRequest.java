package com.hospital.sistema.dto;

import jakarta.validation.constraints.NotNull;

public class ConsultaRequest {

    @NotNull(message = "O ID do médico é obrigatório")
    private Long medicoId;

    @NotNull(message = "O ID do paciente é obrigatório")
    private Long pacienteId;

    @NotNull(message = "O tipo de consulta é obrigatório")
    private Long tipoConsultaId;

    private String diaSemana;
    private String horaInicio;
    private String horaFim;

    public ConsultaRequest() {
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Long getTipoConsultaId() {
        return tipoConsultaId;
    }

    public void setTipoConsultaId(Long tipoConsultaId) {
        this.tipoConsultaId = tipoConsultaId;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }
}
