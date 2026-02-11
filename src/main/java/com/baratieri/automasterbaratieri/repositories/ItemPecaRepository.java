package com.baratieri.automasterbaratieri.repositories;

import com.baratieri.automasterbaratieri.entities.ItemPeca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPecaRepository extends JpaRepository<ItemPeca, Long> {
}
