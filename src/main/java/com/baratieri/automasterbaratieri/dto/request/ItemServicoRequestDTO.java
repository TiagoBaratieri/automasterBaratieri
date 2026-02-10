package com.baratieri.automasterbaratieri.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemServicoRequestDTO(
        @NotNull(message = "O ID da OS é obrigatório")
        Long ordemServicoId,

        @NotNull(message = "O ID do serviço é obrigatório")
        Long servicoId,

        @NotNull(message = "O ID do mecânico é obrigatório")
        Long mecanicoId,

        @Positive(message = "O valor deve ser positivo")
        BigDecimal valorCobrado,

        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade,

        String observacao
) {
}