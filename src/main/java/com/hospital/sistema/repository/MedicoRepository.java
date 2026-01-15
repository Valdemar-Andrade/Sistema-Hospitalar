package com.hospital.sistema.repository;

import com.hospital.sistema.entity.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Page<Medico> findAll(Pageable pageable);

    Optional<Medico> findByLogin(String login);

    @Query("SELECT m FROM Medico m WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%')) " +
           "AND (:especialidadeId IS NULL OR m.especialidade.id = :especialidadeId)")
    List<Medico> buscarPorNomeEEspecialidade(@Param("nome") String nome, @Param("especialidadeId") Long especialidadeId);
}
