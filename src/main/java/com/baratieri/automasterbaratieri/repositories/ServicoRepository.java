package com.baratieri.automasterbaratieri.repositories;
import com.baratieri.automasterbaratieri.entities.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    boolean existsByDescricaoIgnoreCase(String descricao);
}