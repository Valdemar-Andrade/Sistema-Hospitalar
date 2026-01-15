package com.hospital.sistema.repository;

import com.hospital.sistema.entity.TipoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoConsultaRepository extends JpaRepository<TipoConsulta, Long> {

    Optional<TipoConsulta> findByNome(String nome);
}
