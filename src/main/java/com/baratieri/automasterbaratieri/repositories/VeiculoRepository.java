package com.baratieri.automasterbaratieri.repositories;

import com.baratieri.automasterbaratieri.entities.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    Optional<Veiculo> findByPlaca(String placa);

    @Query("SELECT v FROM Veiculo v WHERE " +
    "(:clienteId IS NULL OR v.cliente.id = :clienteId) AND " +
    "(:placa IS NULL OR v.placa = :placa)")
    Page<Veiculo> buscarVeiculosComFiltros(
            @Param("clienteId") Long clienteId,
            @Param("placa") String placa,
            Pageable pageable);

}