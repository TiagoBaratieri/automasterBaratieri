package com.baratieri.automasterbaratieri.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ServicoPayloadDTO(

        @NotNull(message = "O ID do serviço é obrigatório")
        Long servicoId,

        @NotNull(message = "O ID do mecânico é obrigatório")
        Long mecanicoId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade,

        // Observação é opcional, não precisa de validação forte
        String observacao,

        @PositiveOrZero(message = "O valor não pode ser negativo")
        BigDecimal valorCobrado
) {}