package com.baratieri.automasterbaratieri.repositories;

import com.baratieri.automasterbaratieri.entities.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    // A busca mais comum da oficina: "Digita a placa aí"
    Optional<Veiculo> findByPlaca(String placa);

    // Buscar todos os carros de um cliente específico
    List<Veiculo> findByClienteId(Long clienteId);


}