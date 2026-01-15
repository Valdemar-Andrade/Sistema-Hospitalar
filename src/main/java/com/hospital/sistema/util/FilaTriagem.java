package com.hospital.sistema.util;

import com.hospital.sistema.entity.Paciente;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Gerencia a fila de pacientes aguardando triagem.
 */
@Component
public class FilaTriagem {

    private final List<Paciente> pacientes = new ArrayList<>();

    public List<Paciente> listarPacientes() {
        return new ArrayList<>(pacientes);
    }

    public void adicionar(Paciente paciente) {
        if (paciente != null && !contemPaciente(paciente.getId())) {
            pacientes.add(paciente);
        }
    }

    public void remover(Long pacienteId) {
        pacientes.removeIf(p -> p.getId().equals(pacienteId));
    }

    public Optional<Paciente> buscarProximo() {
        if (pacientes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(pacientes.get(0));
    }

    public boolean contemPaciente(Long pacienteId) {
        return pacientes.stream()
                .anyMatch(p -> p.getId().equals(pacienteId));
    }

    public int tamanhoFila() {
        return pacientes.size();
    }
}
