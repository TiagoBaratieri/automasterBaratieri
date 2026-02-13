package com.baratieri.automasterbaratieri.dto.request;

import java.math.BigDecimal;

public record ItemServicoRequestDTO(
        Long ordemServicoId,
        Long servicoId,
        Long mecanicoId,
        BigDecimal valorCobrado,
        Integer quantidade,

        String observacao
) {
}