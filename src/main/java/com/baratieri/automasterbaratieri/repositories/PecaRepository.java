package com.baratieri.automasterbaratieri.repositories;

import com.baratieri.automasterbaratieri.entities.Peca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PecaRepository extends JpaRepository<Peca, Long> {

    // A query usa 'LOWER' para ignorar maiúsculas/minúsculas e 'LIKE' para buscar partes da palavra
    @Query("SELECT p FROM Peca p WHERE " +
            "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:marca IS NULL OR LOWER(p.marca) LIKE LOWER(CONCAT('%', :marca, '%'))) AND " +
            "(:aplicacao IS NULL OR LOWER(p.aplicacao) LIKE LOWER(CONCAT('%', :aplicacao, '%')))")
    Page<Peca> pesquisarPecaEstoque(
            @Param("nome") String nome,
            @Param("marca") String marca,
            @Param("aplicacao") String aplicacao,
            Pageable pageable
    );
    boolean existsBySku(String skuFormatado);

    boolean existsByPartNumber(String partNumber);
}