package com.baratieri.automasterbaratieri.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

public record MecanicoRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "CPF inválido")
        String cpf,

        @NotBlank(message = "A especialidade é obrigatória")
        String especialidade,

        @NotNull(message = "A taxa de comissão é obrigatória")
        @DecimalMin(value = "0.00", message = "A comissão não pode ser negativa")
        @DecimalMax(value = "100.00", message = "A comissão não pode ser maior que 100%")
        @Digits(integer = 3, fraction = 2, message = "Formato inválido (ex: 10.50)")
        BigDecimal taxaComissao,

        @NotNull(message = "O status (ativo/inativo) é obrigatório")
        Boolean ativo
) {}
