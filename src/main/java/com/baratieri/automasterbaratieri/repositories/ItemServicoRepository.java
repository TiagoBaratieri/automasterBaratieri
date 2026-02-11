package com.baratieri.automasterbaratieri.repositories;

import com.baratieri.automasterbaratieri.entities.ItemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemServicoRepository extends JpaRepository<ItemServico, Long> {
}
