package com.baratieri.automasterbaratieri.dto.request;


import com.baratieri.automasterbaratieri.entities.Endereco;

public record ClienteRequestDTO(String nome,
                                String cpfOuCnpj,
                                Endereco endereco,
                                String telefone,
                                String email
                                ) {

}
