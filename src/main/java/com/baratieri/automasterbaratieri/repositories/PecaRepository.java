package com.baratieri.automasterbaratieri.repositories;

import com.baratieri.automasterbaratieri.entities.Peca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PecaRepository extends JpaRepository<Peca, Long> {

    // Busca inteligente: Nome ou PartNumber (Para a barra de pesquisa geral)
    // Query: SELECT * FROM peca WHERE nome LIKE %x% OR part_number LIKE %x%
    List<Peca> findByNomeContainingIgnoreCaseOrPartNumberContainingIgnoreCase(String nome, String partNumber);
}