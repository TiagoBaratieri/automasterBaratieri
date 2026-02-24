package com.baratieri.automasterbaratieri.repositories;
import com.baratieri.automasterbaratieri.entities.Mecanico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {
    boolean existsByCpf(String cpf);
    Optional<Mecanico> findByIdAndAtivoTrue(Long id);

    @Query("SELECT m FROM Mecanico m WHERE " +
            "(:nome IS NULL OR LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:especialidade IS NULL OR LOWER(m.especialidade) LIKE LOWER(CONCAT('%', :especialidade, '%'))) AND " +
            "(:ativo IS NULL OR m.ativo = :ativo)")
    Page<Mecanico> buscarMecanicoComFiltro(
            @Param("nome") String nome,
            @Param("especialidade") String especialidade,
            @Param("ativo") Boolean ativo,
            Pageable pageable);
}