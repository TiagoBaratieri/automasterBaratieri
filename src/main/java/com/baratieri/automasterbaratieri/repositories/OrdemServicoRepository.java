package com.baratieri.automasterbaratieri.repositories;


import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.entities.Veiculo;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface OrdemServicoRepository extends JpaRepository<com.baratieri.automasterbaratieri.entities.OrdemServico, Long> {

    boolean existsByVeiculoAndStatusIn(Veiculo veiculo, List<StatusOS> orcamento);
}