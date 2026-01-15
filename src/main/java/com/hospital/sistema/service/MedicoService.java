package com.hospital.sistema.service;

import com.hospital.sistema.dto.MedicoResponse;
import com.hospital.sistema.entity.Medico;
import com.hospital.sistema.exception.RecursoNaoEncontradoException;
import com.hospital.sistema.repository.MedicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public Page<Medico> listarTodos(Pageable pageable) {
        return medicoRepository.findAll(pageable);
    }

    public Medico buscarPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico", id));
    }

    public Optional<Medico> buscarPorLogin(String login) {
        return medicoRepository.findByLogin(login);
    }

    public List<MedicoResponse> buscarPorNomeEEspecialidade(String nome, Long especialidadeId) {
        return medicoRepository.buscarPorNomeEEspecialidade(nome, especialidadeId).stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Medico salvar(Medico medico) {
        return medicoRepository.save(medico);
    }

    @Transactional
    public void deletar(Long id) {
        if (!medicoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Médico", id);
        }
        medicoRepository.deleteById(id);
    }

    private MedicoResponse converterParaResponse(Medico medico) {
        return new MedicoResponse(medico.getId(), medico.getNome(), medico.getEspecialidade());
    }
}
