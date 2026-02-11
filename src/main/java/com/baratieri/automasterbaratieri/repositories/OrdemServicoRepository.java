package com.baratieri.automasterbaratieri.repositories;


import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.entities.Veiculo;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface OrdemServicoRepository extends JpaRepository<com.baratieri.automasterbaratieri.entities.OrdemServico, Long> {

    List<OrdemServico> findByStatus(StatusOS status);

    // Histórico do Cliente: Ver tudo que aquele carro já fez
    List<OrdemServico> findByVeiculoPlaca(String placa);

    boolean existsByVeiculoAndStatusIn(Veiculo veiculo, List<StatusOS> orcamento);
}