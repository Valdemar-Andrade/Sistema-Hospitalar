package com.hospital.sistema.service;

import com.hospital.sistema.dto.PacienteRequest;
import com.hospital.sistema.dto.PacienteResponse;
import com.hospital.sistema.entity.Documento;
import com.hospital.sistema.entity.Endereco;
import com.hospital.sistema.entity.Paciente;
import com.hospital.sistema.enums.Sexo;
import com.hospital.sistema.enums.TipoDocumento;
import com.hospital.sistema.exception.DocumentoJaCadastradoException;
import com.hospital.sistema.exception.RecursoNaoEncontradoException;
import com.hospital.sistema.exception.ValidacaoException;
import com.hospital.sistema.repository.DocumentoRepository;
import com.hospital.sistema.repository.PacienteRepository;
import com.hospital.sistema.util.ValidadorDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    private static final LocalDate DATA_LIMITE_INFERIOR = LocalDate.of(1900, 1, 1);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final PacienteRepository pacienteRepository;
    private final DocumentoRepository documentoRepository;

    public PacienteService(PacienteRepository pacienteRepository, DocumentoRepository documentoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.documentoRepository = documentoRepository;
    }

    public Page<Paciente> listarTodos(Pageable pageable) {
        return pacienteRepository.findAll(pageable);
    }

    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente", id));
    }

    public List<PacienteResponse> buscarPorNome(String nome) {
        return pacienteRepository.buscarPorNome(nome).stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Paciente salvar(PacienteRequest request) {
        validarDocumentoNaoCadastrado(request.getNumeroDocumento());

        TipoDocumento tipoDoc = TipoDocumento.valueOf(request.getTipoDocumento());
        validarFormatoDocumento(tipoDoc, request.getNumeroDocumento());

        LocalDate dataNascimento = parsearData(request.getDataNascimento());
        validarDataNascimento(dataNascimento);
        validarTelefone(request.getTelefone());

        Paciente paciente = criarPaciente(request, tipoDoc, dataNascimento);
        return pacienteRepository.save(paciente);
    }

    @Transactional
    public Paciente atualizar(Long id, PacienteRequest request) {
        Paciente paciente = buscarPorId(id);

        LocalDate dataNascimento = parsearData(request.getDataNascimento());
        validarDataNascimento(dataNascimento);
        validarTelefone(request.getTelefone());

        atualizarDadosPaciente(paciente, request, dataNascimento);
        return pacienteRepository.save(paciente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Paciente", id);
        }
        pacienteRepository.deleteById(id);
    }

    private void validarDocumentoNaoCadastrado(String numeroDocumento) {
        if (documentoRepository.existsByNumero(numeroDocumento)) {
            throw new DocumentoJaCadastradoException(numeroDocumento);
        }
    }

    private void validarFormatoDocumento(TipoDocumento tipo, String numero) {
        if (!ValidadorDocumento.isValido(tipo, numero)) {
            throw new ValidacaoException("Formato do documento inválido");
        }
    }

    private void validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new ValidacaoException("A data de nascimento é obrigatória");
        }

        if (dataNascimento.isAfter(LocalDate.now().minusDays(1))) {
            throw new ValidacaoException("A data de nascimento não pode ser hoje ou no futuro");
        }

        if (dataNascimento.isBefore(DATA_LIMITE_INFERIOR)) {
            throw new ValidacaoException("A data de nascimento não pode ser anterior a 1900");
        }
    }

    private void validarTelefone(String telefone) {
        if (telefone != null && telefone.length() != 9) {
            throw new ValidacaoException("O telefone deve ter 9 dígitos");
        }
    }

    private LocalDate parsearData(String data) {
        return LocalDate.parse(data, FORMATO_DATA);
    }

    private Paciente criarPaciente(PacienteRequest request, TipoDocumento tipoDoc, LocalDate dataNascimento) {
        Paciente paciente = new Paciente();

        Documento documento = new Documento(tipoDoc, request.getNumeroDocumento());
        Endereco endereco = new Endereco(request.getEndereco());

        paciente.setNome(request.getNome());
        paciente.setDocumento(documento);
        paciente.setEndereco(endereco);
        paciente.setTelefone(request.getTelefone());
        paciente.setEmail(request.getEmail());
        paciente.setDataNascimento(dataNascimento);

        if (request.getSexo() != null && !request.getSexo().isBlank()) {
            paciente.setSexo(Sexo.valueOf(request.getSexo()));
        }

        return paciente;
    }

    private void atualizarDadosPaciente(Paciente paciente, PacienteRequest request, LocalDate dataNascimento) {
        paciente.setNome(request.getNome());
        paciente.setTelefone(request.getTelefone());
        paciente.setEmail(request.getEmail());
        paciente.setDataNascimento(dataNascimento);

        if (paciente.getDocumento() != null) {
            paciente.getDocumento().setTipo(TipoDocumento.valueOf(request.getTipoDocumento()));
            paciente.getDocumento().setNumero(request.getNumeroDocumento());
        }

        if (paciente.getEndereco() != null) {
            paciente.getEndereco().setDescricao(request.getEndereco());
        }

        if (request.getSexo() != null && !request.getSexo().isBlank()) {
            paciente.setSexo(Sexo.valueOf(request.getSexo()));
        }
    }

    private PacienteResponse converterParaResponse(Paciente paciente) {
        String documento = paciente.getDocumento() != null ? paciente.getDocumento().getNumero() : null;
        return new PacienteResponse(paciente.getId(), paciente.getNome(), documento, paciente.getTelefone());
    }
}
