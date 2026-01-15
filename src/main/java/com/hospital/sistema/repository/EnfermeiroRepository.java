package com.hospital.sistema.repository;

import com.hospital.sistema.entity.Enfermeiro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnfermeiroRepository extends JpaRepository<Enfermeiro, Long> {

    Page<Enfermeiro> findAll(Pageable pageable);

    Optional<Enfermeiro> findByLogin(String login);
}
