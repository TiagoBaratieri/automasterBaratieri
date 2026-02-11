package com.baratieri.automasterbaratieri.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AdicionarServicoRequestDTO(
        @NotNull(message = "O ID do serviço é obrigatório")
        Long idServico,

        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade,

        // Pode ser nulo (usa o padrão), mas se vier, não pode ser negativo
        @PositiveOrZero(message = "O valor cobrado não pode ser negativo")
        BigDecimal valorCobrado,

        @NotBlank(message = "A dbservação é obrigatória")
        String observacao) {

}




