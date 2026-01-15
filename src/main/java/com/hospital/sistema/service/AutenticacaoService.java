package com.hospital.sistema.service;

import com.hospital.sistema.config.SessaoUsuario;
import com.hospital.sistema.entity.*;
import com.hospital.sistema.enums.TipoUsuario;
import com.hospital.sistema.exception.CredenciaisInvalidasException;
import com.hospital.sistema.repository.*;
import com.hospital.sistema.util.SenhaUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço responsável pela autenticação de usuários.
 */
@Service
public class AutenticacaoService {

    private final SessaoUsuario sessaoUsuario;
    private final AdminRepository adminRepository;
    private final MedicoRepository medicoRepository;
    private final EnfermeiroRepository enfermeiroRepository;
    private final RecepcionistaRepository recepcionistaRepository;

    public AutenticacaoService(SessaoUsuario sessaoUsuario,
                               AdminRepository adminRepository,
                               MedicoRepository medicoRepository,
                               EnfermeiroRepository enfermeiroRepository,
                               RecepcionistaRepository recepcionistaRepository) {
        this.sessaoUsuario = sessaoUsuario;
        this.adminRepository = adminRepository;
        this.medicoRepository = medicoRepository;
        this.enfermeiroRepository = enfermeiroRepository;
        this.recepcionistaRepository = recepcionistaRepository;
    }

    /**
     * Autentica o usuário e retorna o tipo de usuário se bem-sucedido.
     */
    public String autenticar(String login, String senha) {
        // Tenta autenticar como cada tipo de usuário
        if (autenticarAdmin(login, senha)) {
            return TipoUsuario.ADMIN.getTipo();
        }

        if (autenticarMedico(login, senha)) {
            return TipoUsuario.MEDICO.getTipo();
        }

        if (autenticarEnfermeiro(login, senha)) {
            return TipoUsuario.ENFERMEIRO.getTipo();
        }

        if (autenticarRecepcionista(login, senha)) {
            return TipoUsuario.RECEPCIONISTA.getTipo();
        }

        throw new CredenciaisInvalidasException();
    }

    private boolean autenticarAdmin(String login, String senha) {
        Optional<Admin> admin = adminRepository.findByLogin(login);

        if (admin.isPresent() && verificarSenha(senha, admin.get().getSenha())) {
            configurarSessao(admin.get().getNome(), admin.get().getId(), TipoUsuario.ADMIN.getTipo());
            return true;
        }
        return false;
    }

    private boolean autenticarMedico(String login, String senha) {
        Optional<Medico> medico = medicoRepository.findByLogin(login);

        if (medico.isPresent() && verificarSenha(senha, medico.get().getSenha())) {
            configurarSessao(medico.get().getNome(), medico.get().getId(), TipoUsuario.MEDICO.getTipo());
            return true;
        }
        return false;
    }

    private boolean autenticarEnfermeiro(String login, String senha) {
        Optional<Enfermeiro> enfermeiro = enfermeiroRepository.findByLogin(login);

        if (enfermeiro.isPresent() && verificarSenha(senha, enfermeiro.get().getSenha())) {
            configurarSessao(enfermeiro.get().getNome(), enfermeiro.get().getId(), TipoUsuario.ENFERMEIRO.getTipo());
            return true;
        }
        return false;
    }

    private boolean autenticarRecepcionista(String login, String senha) {
        Optional<Recepcionista> recepcionista = recepcionistaRepository.findByLogin(login);

        if (recepcionista.isPresent() && verificarSenha(senha, recepcionista.get().getSenha())) {
            configurarSessao(recepcionista.get().getNome(), recepcionista.get().getId(), TipoUsuario.RECEPCIONISTA.getTipo());
            return true;
        }
        return false;
    }

    private boolean verificarSenha(String senhaDigitada, String senhaCadastrada) {
        // Verifica senha criptografada ou em texto plano (para compatibilidade)
        return SenhaUtils.verificar(senhaDigitada, senhaCadastrada)
                || senhaDigitada.equals(senhaCadastrada);
    }

    private void configurarSessao(String nome, Long id, String tipo) {
        sessaoUsuario.setNomeUsuario(nome);
        sessaoUsuario.setIdUsuario(id);
        sessaoUsuario.setTipoUsuario(tipo);
    }
}
