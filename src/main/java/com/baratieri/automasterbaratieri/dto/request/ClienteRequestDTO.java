package com.baratieri.automasterbaratieri.dto.request;


import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.entities.Endereco;

public record ClienteRequestDTO(String nome,
                                String cpfOuCnpj,
                                Endereco endereco,
                                String telefone,
                                String email
                                ) {

    public Cliente toEntity() {
        Cliente cliente = new Cliente();
        cliente.setNome(this.nome);
        cliente.setCpfOuCnpj(this.cpfOuCnpj);
        cliente.setEndereco(endereco);
        cliente.setTelefone(this.telefone);
        cliente.setEmail(this.email);
        return cliente;
    }
}
