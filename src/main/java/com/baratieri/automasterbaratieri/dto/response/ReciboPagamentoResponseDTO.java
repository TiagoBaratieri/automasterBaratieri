package com.baratieri.automasterbaratieri.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReciboPagamentoResponseDTO(
        String tipoPagamento,
        BigDecimal valor,
        String detalhes,
        LocalDateTime dataPagamento
) {
}
