package com.hospital.sistema.service;

import com.hospital.sistema.dto.PacienteFilaDTO;
import com.hospital.sistema.dto.TriagemRequest;
import com.hospital.sistema.entity.Consulta;
import com.hospital.sistema.entity.Enfermeiro;
import com.hospital.sistema.entity.Paciente;
import com.hospital.sistema.entity.Triagem;
import com.hospital.sistema.enums.NivelUrgencia;
import com.hospital.sistema.enums.StatusConsulta;
import com.hospital.sistema.exception.RecursoNaoEncontradoException;
import com.hospital.sistema.repository.EnfermeiroRepository;
import com.hospital.sistema.repository.TriagemRepository;
import com.hospital.sistema.util.FilaAtendimentoMedico;
import com.hospital.sistema.util.FilaTriagem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TriagemService {

    private final TriagemRepository triagemRepository;
    private final PacienteService pacienteService;
    private final ConsultaService consultaService;
    private final EnfermeiroRepository enfermeiroRepository;
    private final FilaTriagem filaTriagem;
    private final FilaAtendimentoMedico filaAtendimentoMedico;

    public TriagemService(TriagemRepository triagemRepository,
                         PacienteService pacienteService,
                         ConsultaService consultaService,
                         EnfermeiroRepository enfermeiroRepository,
                         FilaTriagem filaTriagem,
                         FilaAtendimentoMedico filaAtendimentoMedico) {
        this.triagemRepository = triagemRepository;
        this.pacienteService = pacienteService;
        this.consultaService = consultaService;
        this.enfermeiroRepository = enfermeiroRepository;
        this.filaTriagem = filaTriagem;
        this.filaAtendimentoMedico = filaAtendimentoMedico;
    }

    public List<Paciente> listarFilaTriagem() {
        return filaTriagem.listarPacientes();
    }

    public List<Triagem> buscarHistoricoPaciente(Long pacienteId) {
        return triagemRepository.findByPacienteIdOrderByDataHoraDesc(pacienteId);
    }

    @Transactional
    public Triagem realizar(TriagemRequest request, Long enfermeiroId) {
        Paciente paciente = pacienteService.buscarPorId(request.getPacienteId());
        Enfermeiro enfermeiro = buscarEnfermeiro(enfermeiroId);

        Triagem triagem = criarTriagem(request, paciente, enfermeiro);
        Triagem triagemSalva = triagemRepository.save(triagem);

        // Atualiza a consulta e encaminha para fila do médico
        processarPosTriagem(request, paciente, triagemSalva);

        return triagemSalva;
    }

    private Enfermeiro buscarEnfermeiro(Long id) {
        return enfermeiroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Enfermeiro", id));
    }

    private Triagem criarTriagem(TriagemRequest request, Paciente paciente, Enfermeiro enfermeiro) {
        Triagem triagem = new Triagem();

        triagem.setPaciente(paciente);
        triagem.setEnfermeiro(enfermeiro);
        triagem.setDataHora(LocalDateTime.now());
        triagem.setPeso(request.getPeso());
        triagem.setAltura(request.getAltura());
        triagem.setTemperatura(request.getTemperatura());
        triagem.setPressaoArterial(request.getPressaoArterial());
        triagem.setFrequenciaCardiaca(request.getFrequenciaCardiaca());
        triagem.setSintomas(request.getSintomas());
        triagem.setObservacoes(request.getObservacoes());
        triagem.setNivelUrgencia(NivelUrgencia.valueOf(request.getNivelUrgencia()));

        return triagem;
    }

    private void processarPosTriagem(TriagemRequest request, Paciente paciente, Triagem triagem) {
        filaTriagem.remover(paciente.getId());

        if (request.getConsultaId() != null) {
            Consulta consulta = consultaService.buscarPorId(request.getConsultaId());
            consulta.setStatus(StatusConsulta.AGUARDANDO_ATENDIMENTO);
            triagem.setConsulta(consulta);
            consultaService.salvar(consulta);

            // Adiciona na fila do médico
            PacienteFilaDTO pacienteFila = new PacienteFilaDTO(
                    paciente.getId(),
                    paciente.getNome(),
                    consulta.getMedico().getId(),
                    consulta.getId(),
                    triagem.getNivelUrgencia().name()
            );
            filaAtendimentoMedico.adicionar(pacienteFila);
        }
    }
}
