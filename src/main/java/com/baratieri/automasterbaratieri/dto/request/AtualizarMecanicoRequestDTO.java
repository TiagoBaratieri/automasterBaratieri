package com.baratieri.automasterbaratieri.dto.request;

import java.math.BigDecimal;

public record AtualizarMecanicoRequestDTO(
        BigDecimal taxaComissao,
        Boolean ativo) {
}
