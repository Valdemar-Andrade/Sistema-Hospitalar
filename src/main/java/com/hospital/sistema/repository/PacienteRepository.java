package com.hospital.sistema.repository;

import com.hospital.sistema.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Page<Paciente> findAll(Pageable pageable);

    @Query("SELECT p FROM Paciente p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Paciente> buscarPorNome(@Param("nome") String nome);

    @Query("SELECT p FROM Paciente p WHERE p.documento.numero = :numero")
    Paciente buscarPorDocumento(@Param("numero") String numero);
}
