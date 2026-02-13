package com.baratieri.automasterbaratieri.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AdicionarPecaPayloadDTO(
        @NotNull(message = "O ID da peça é obrigatório")
        Long pecaId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade,

        @PositiveOrZero
        BigDecimal valorUnitario
) {
}