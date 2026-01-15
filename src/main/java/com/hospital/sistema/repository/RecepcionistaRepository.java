package com.hospital.sistema.repository;

import com.hospital.sistema.entity.Recepcionista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecepcionistaRepository extends JpaRepository<Recepcionista, Long> {

    Page<Recepcionista> findAll(Pageable pageable);

    Optional<Recepcionista> findByLogin(String login);
}
