package com.baratieri.automasterbaratieri.dto.request;

import java.math.BigDecimal;

public record AtualizarPecaRequestDTO(BigDecimal precoVenda,
                                      BigDecimal precoCusto,
                                      Integer estoqueMinimo) {
}
