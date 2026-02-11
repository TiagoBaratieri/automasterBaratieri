package com.baratieri.automasterbaratieri.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ServicoRequestDTO(

        @NotBlank(message = "A descrição do serviço é obrigatória")
        String descricao,
        @NotNull(message = "O preço base é obrigatório")
        @PositiveOrZero(message = "O preço não pode ser negativo")
        BigDecimal valorMaoDeObraBase) {

}
