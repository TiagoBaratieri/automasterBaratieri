package com.baratieri.automasterbaratieri.repositories;

import com.baratieri.automasterbaratieri.entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}