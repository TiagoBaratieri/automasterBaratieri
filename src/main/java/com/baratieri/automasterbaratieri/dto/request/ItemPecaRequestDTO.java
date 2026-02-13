package com.baratieri.automasterbaratieri.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemPecaRequestDTO(

        @NotNull(message = "O ID da Ordem de Serviço é obrigatório")
        Long ordemServicoId,
        Long pecaId,
        Integer quantidade,
        BigDecimal valorUnitarioCobrado
) {
}


