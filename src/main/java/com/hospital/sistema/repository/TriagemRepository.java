package com.hospital.sistema.repository;

import com.hospital.sistema.entity.Triagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TriagemRepository extends JpaRepository<Triagem, Long> {

    List<Triagem> findByPacienteIdOrderByDataHoraDesc(Long pacienteId);
}
