package com.baratieri.automasterbaratieri.dto.response;

import com.baratieri.automasterbaratieri.entities.Pagamento;
import com.baratieri.automasterbaratieri.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public record PagamentoResponseDTO(
        Long id,
        BigDecimal valor,
        StatusPagamento status,
        LocalDateTime dataPagamento,
        String tipoPagamento,
        String detalhes,
        BigDecimal valorRecebido,
        BigDecimal troco
) {
    public static PagamentoResponseDTO fromEntity(Pagamento pagamento) {
        Objects.requireNonNull(pagamento, "A entidade Pagamento não pode ser nula ao gerar o DTO");

        return new PagamentoResponseDTO(
                pagamento.getId(),
                pagamento.getValor(),
                pagamento.getStatusPagamento(),
                pagamento.getDataPagamento(),
                null, // tipoPagamento
                null, // detalhes
                null, // valorRecebido
                null  // troco
        );
    }
}
