package com.baratieri.automasterbaratieri.dto.request;

import com.baratieri.automasterbaratieri.entities.Endereco;
import jakarta.validation.constraints.Email;

public record ClienteAtualizacaoRequestDTO(Endereco endereco,

                                           String telefone,

                                           @Email(message = "Formato de e-mail inválido")
                                           String email) {
}
