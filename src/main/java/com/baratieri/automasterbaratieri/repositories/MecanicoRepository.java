package com.baratieri.automasterbaratieri.repositories;
import com.baratieri.automasterbaratieri.entities.Mecanico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {

    Optional<Mecanico> findByIdAndAtivoTrue(Long id);
}