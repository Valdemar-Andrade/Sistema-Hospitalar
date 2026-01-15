package com.hospital.sistema.repository;

import com.hospital.sistema.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    Optional<Documento> findByNumero(String numero);

    boolean existsByNumero(String numero);
}
