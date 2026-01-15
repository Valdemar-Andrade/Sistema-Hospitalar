package com.hospital.sistema.repository;

import com.hospital.sistema.entity.Consulta;
import com.hospital.sistema.enums.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByPacienteIdOrderByDataDesc(Long pacienteId);

    List<Consulta> findByMedicoIdOrderByDataDesc(Long medicoId);

    @Query("SELECT c FROM Consulta c WHERE c.paciente.id = :pacienteId AND c.status = :status")
    List<Consulta> buscarPorPacienteEStatus(@Param("pacienteId") Long pacienteId, @Param("status") StatusConsulta status);

    @Query("SELECT c FROM Consulta c WHERE c.data = :data AND c.status = :status")
    List<Consulta> buscarPorDataEStatus(@Param("data") LocalDate data, @Param("status") StatusConsulta status);

    @Query("SELECT c FROM Consulta c WHERE c.medico.id = :medicoId AND c.data = :data AND c.status = :status")
    List<Consulta> buscarPorMedicoDataEStatus(
            @Param("medicoId") Long medicoId,
            @Param("data") LocalDate data,
            @Param("status") StatusConsulta status
    );
}
