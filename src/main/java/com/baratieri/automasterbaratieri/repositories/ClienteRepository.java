package com.baratieri.automasterbaratieri.repositories;

import com.baratieri.automasterbaratieri.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c WHERE " +
            "(:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:cpfOuCnpj IS NULL OR c.cpfOuCnpj = :cpfOuCnpj)")
    Page<Cliente> buscarClentesComFiltros(
            @Param("nome") String nome,
            @Param("cpfOuCnpj") String cpfOuCnpj,
            Pageable pageable
    );

    boolean existsByCpfOuCnpj(String cpf);
}
