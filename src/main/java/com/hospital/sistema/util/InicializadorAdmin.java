package com.hospital.sistema.util;

import com.hospital.sistema.entity.Admin;
import com.hospital.sistema.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializa uma conta de administrador padrão caso não exista nenhuma.
 */
@Component
public class InicializadorAdmin implements CommandLineRunner {

    private final AdminRepository adminRepository;

    public InicializadorAdmin(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... args) {
        criarAdminPadraoSeNecessario();
    }

    private void criarAdminPadraoSeNecessario() {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setNome("Administrador");
            admin.setLogin("admin");
            admin.setSenha(SenhaUtils.criptografar("admin123"));

            adminRepository.save(admin);
            System.out.println("Conta de administrador padrão criada: admin/admin123");
        }
    }
}
