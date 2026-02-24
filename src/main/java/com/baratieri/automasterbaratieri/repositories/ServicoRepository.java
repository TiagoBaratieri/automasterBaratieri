package com.baratieri.automasterbaratieri.repositories;
import com.baratieri.automasterbaratieri.entities.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    @Query("SELECT s FROM Servico s WHERE " +
            "(:descricao IS NULL OR LOWER(s.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')))")
    Page<Servico> buscarServicosComFiltros(
            @Param("descricao") String descricao,
            Pageable pageable
    );
    boolean existsByDescricaoIgnoreCase(String descricao);
}