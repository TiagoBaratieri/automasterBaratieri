package com.baratieri.automasterbaratieri.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ItemPecaRequestDTO(

        @NotNull(message = "O ID da Ordem de Serviço é obrigatório")
        Long ordemServicoId,

        @NotNull(message = "O ID da peça é obrigatório")
        Long pecaId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade,

        @PositiveOrZero(message = "O valor não pode ser negativo")
        BigDecimal valorUnitarioCobrado
) {
}


