package com.hospital.sistema.util;

import com.hospital.sistema.dto.PacienteFilaDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia a fila de pacientes aguardando atendimento médico.
 */
@Component
public class FilaAtendimentoMedico {

    private final List<PacienteFilaDTO> pacientes = new ArrayList<>();

    public List<PacienteFilaDTO> listarTodos() {
        return new ArrayList<>(pacientes);
    }

    /**
     * Lista pacientes na fila de um médico específico.
     */
    public List<PacienteFilaDTO> listarPorMedico(Long medicoId) {
        return pacientes.stream()
                .filter(p -> p.getMedicoId().equals(medicoId))
                .collect(Collectors.toList());
    }

    public void adicionar(PacienteFilaDTO paciente) {
        if (paciente != null) {
            pacientes.add(paciente);
        }
    }

    public void remover(Long pacienteId) {
        pacientes.removeIf(p -> p.getId().equals(pacienteId));
    }

    public int tamanhoFilaPorMedico(Long medicoId) {
        return (int) pacientes.stream()
                .filter(p -> p.getMedicoId().equals(medicoId))
                .count();
    }
}
