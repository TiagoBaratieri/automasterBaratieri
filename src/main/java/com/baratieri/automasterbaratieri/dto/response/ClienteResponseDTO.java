package com.baratieri.automasterbaratieri.dto.response;


import com.baratieri.automasterbaratieri.entities.Cliente;

public record ClienteResponseDTO(Long id,
                                 String nome,
                                 String cpfOuCnpj,
                                 String telefone,
                                 String email,
                                 EnderecoResponseDTO endereco) {

    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        if (cliente == null) return null;

        EnderecoResponseDTO enderecoDTO = EnderecoResponseDTO.fromEntity(cliente.getEndereco());

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpfOuCnpj(),
                cliente.getTelefone(),
                cliente.getEmail(),
                enderecoDTO // Passa o objeto convertido
        );
    }
}
