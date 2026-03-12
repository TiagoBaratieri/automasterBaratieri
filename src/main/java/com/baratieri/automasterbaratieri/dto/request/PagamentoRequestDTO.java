package com.baratieri.automasterbaratieri.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PagamentoRequestDTO(
        @NotBlank(message = "O tipo de pagamento é obrigatório (PIX, CARTAO ou DINHEIRO).")
        String tipoPagamento,
        @NotNull(message = "O valor do pagamento é obrigatório.")
        @Positive(message = "O valor do pagamento deve ser maior que zero.")
        BigDecimal valor,
        Integer parcelas,
        String bandeira,
        String chavePix,
        BigDecimal valorRecebido) {
}
