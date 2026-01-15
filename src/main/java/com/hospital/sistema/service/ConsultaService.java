package com.hospital.sistema.service;

import com.hospital.sistema.dto.ConsultaRequest;
import com.hospital.sistema.dto.ConsultaResponse;
import com.hospital.sistema.entity.Consulta;
import com.hospital.sistema.entity.Medico;
import com.hospital.sistema.entity.Paciente;
import com.hospital.sistema.entity.TipoConsulta;
import com.hospital.sistema.enums.StatusConsulta;
import com.hospital.sistema.exception.RecursoNaoEncontradoException;
import com.hospital.sistema.repository.ConsultaRepository;
import com.hospital.sistema.repository.TipoConsultaRepository;
import com.hospital.sistema.util.FilaTriagem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    private static final List<String> DIAS_SEMANA = Arrays.asList(
            "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"
    );

    private final ConsultaRepository consultaRepository;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;
    private final TipoConsultaRepository tipoConsultaRepository;
    private final FilaTriagem filaTriagem;

    public ConsultaService(ConsultaRepository consultaRepository,
                          PacienteService pacienteService,
                          MedicoService medicoService,
                          TipoConsultaRepository tipoConsultaRepository,
                          FilaTriagem filaTriagem) {
        this.consultaRepository = consultaRepository;
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
        this.tipoConsultaRepository = tipoConsultaRepository;
        this.filaTriagem = filaTriagem;
    }

    public Consulta buscarPorId(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta", id));
    }

    public List<ConsultaResponse> buscarConsultasDeHoje() {
        LocalDate hoje = LocalDate.now();
        List<Consulta> consultas = consultaRepository.buscarPorDataEStatus(hoje, StatusConsulta.AGENDADA);
        return consultas.stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    public List<Consulta> buscarHistoricoPaciente(Long pacienteId) {
        return consultaRepository.findByPacienteIdOrderByDataDesc(pacienteId);
    }

    @Transactional
    public Optional<Consulta> agendar(ConsultaRequest request) {
        try {
            Medico medico = medicoService.buscarPorId(request.getMedicoId());
            Paciente paciente = pacienteService.buscarPorId(request.getPacienteId());
            TipoConsulta tipoConsulta = buscarTipoConsulta(request.getTipoConsultaId());

            Consulta consulta = criarConsulta(request, medico, paciente, tipoConsulta);
            return Optional.of(consultaRepository.save(consulta));

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Consulta salvar(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    /**
     * Encaminha o paciente de uma consulta para a triagem.
     */
    public boolean encaminharParaTriagem(Long consultaId) {
        Optional<Consulta> consultaOpt = consultaRepository.findById(consultaId);

        if (consultaOpt.isPresent()) {
            Paciente paciente = consultaOpt.get().getPaciente();
            if (paciente != null) {
                filaTriagem.adicionar(paciente);
                return true;
            }
        }
        return false;
    }

    public void removerPacienteDaFilaTriagem(Long pacienteId) {
        filaTriagem.remover(pacienteId);
    }

    public Optional<Consulta> buscarConsultaAgendadaPorPaciente(Long pacienteId) {
        List<Consulta> consultas = consultaRepository.buscarPorPacienteEStatus(pacienteId, StatusConsulta.AGENDADA);

        if (consultas.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(consultas.get(consultas.size() - 1));
    }

    private TipoConsulta buscarTipoConsulta(Long id) {
        return tipoConsultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de Consulta", id));
    }

    private Consulta criarConsulta(ConsultaRequest request, Medico medico, Paciente paciente, TipoConsulta tipoConsulta) {
        Consulta consulta = new Consulta();

        consulta.setMedico(medico);
        consulta.setPaciente(paciente);
        consulta.setTipoConsulta(tipoConsulta);
        consulta.setData(calcularProximaData(request.getDiaSemana()));
        consulta.setHoraInicio(LocalTime.parse(request.getHoraInicio()));
        consulta.setHoraFim(LocalTime.parse(request.getHoraFim()));
        consulta.setStatus(StatusConsulta.AGENDADA);

        return consulta;
    }

    /**
     * Calcula a próxima data com base no dia da semana informado.
     */
    private LocalDate calcularProximaData(String diaSemana) {
        int indiceDiaSemana = DIAS_SEMANA.indexOf(diaSemana.toUpperCase());

        if (indiceDiaSemana == -1) {
            throw new IllegalArgumentException("Dia da semana inválido: " + diaSemana);
        }

        LocalDate hoje = LocalDate.now();
        int indiceHoje = hoje.getDayOfWeek().getValue() % 7;
        int diasParaAdicionar = (indiceDiaSemana - indiceHoje + 7) % 7;

        return hoje.plusDays(diasParaAdicionar);
    }

    private ConsultaResponse converterParaResponse(Consulta consulta) {
        ConsultaResponse response = new ConsultaResponse();

        response.setId(consulta.getId());
        response.setPaciente(consulta.getPaciente().getNome());
        response.setMedico(consulta.getMedico().getNome());
        response.setEspecialidade(consulta.getMedico().getEspecialidade().getNome());
        response.setTipoConsulta(consulta.getTipoConsulta().getNome());
        response.setData(consulta.getData());
        response.setHoraInicio(consulta.getHoraInicio());
        response.setHoraFim(consulta.getHoraFim());
        response.setStatus(consulta.getStatus().getDescricao());

        return response;
    }
}
