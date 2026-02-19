package com.baratieri.automasterbaratieri.repositories;

import com.baratieri.automasterbaratieri.entities.Cliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNomeIgnoreCaseContaining(String nome);

    boolean existsByCpfOuCnpj(String cpf);
}
