package com.hospital.sistema.service;

import com.hospital.sistema.entity.Admin;
import com.hospital.sistema.exception.RecursoNaoEncontradoException;
import com.hospital.sistema.repository.AdminRepository;
import com.hospital.sistema.util.SenhaUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Admin buscarPorId(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Administrador", id));
    }

    @Transactional
    public Optional<Admin> atualizarCredenciais(Long id, String novoLogin, String novaSenha) {
        return adminRepository.findById(id).map(admin -> {
            admin.setLogin(novoLogin);
            admin.setSenha(SenhaUtils.criptografar(novaSenha));
            return adminRepository.save(admin);
        });
    }
}
