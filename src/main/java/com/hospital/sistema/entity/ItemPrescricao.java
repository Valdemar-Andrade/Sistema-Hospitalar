package com.hospital.sistema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "itens_prescricao")
public class ItemPrescricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do medicamento é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String medicamento;

    @Size(max = 50, message = "A dosagem deve ter no máximo 50 caracteres")
    @Column(length = 50)
    private String dosagem;

    @Size(max = 100, message = "A frequência deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String frequencia;

    @Size(max = 255, message = "As observações devem ter no máximo 255 caracteres")
    private String observacoes;

    public ItemPrescricao() {
    }

    public ItemPrescricao(String medicamento, String dosagem, String frequencia, String observacoes) {
        this.medicamento = medicamento;
        this.dosagem = dosagem;
        this.frequencia = frequencia;
        this.observacoes = observacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public String toString() {
        return medicamento + " - " + dosagem + " (" + frequencia + ")";
    }
}
