package com.baratieri.automasterbaratieri.repositories;
import com.baratieri.automasterbaratieri.entities.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    // Busca serviços pelo nome (ex: buscar "Freio" traz tudo relacionado)
    List<Servico> findByDescricaoContainingIgnoreCase(String descricao);
}