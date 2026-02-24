package com.baratieri.automasterbaratieri.repositories;


import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.entities.Veiculo;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemServicoRepository extends JpaRepository<com.baratieri.automasterbaratieri.entities.OrdemServico, Long> {

    boolean existsByVeiculoAndStatusIn(Veiculo veiculo, List<StatusOS> orcamento);

    @Query("SELECT os FROM OrdemServico os WHERE " +
            "(:placa IS NULL OR os.veiculo.placa = :placa) AND " +
            "(: status IS NULL OR os.status = :status)")
    Page<OrdemServico> buscarOrdemServicoComFiltros(@Param("placa") String placa,
                                          @Param("status") StatusOS status,
                                          Pageable pageable);
}