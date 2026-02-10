package com.baratieri.automasterbaratieri.dto.request;

import com.baratieri.automasterbaratieri.validation.Placa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record VeiculoRequestDTO(

        @Placa
        String placa,

        @NotBlank(message = "O modelo é obrigatório")
        String modelo,

        @NotBlank(message = "A marca é obrigatória")
        String marca,

        @NotNull(message = "O ano é obrigatório")
        @Positive(message = "O ano deve ser válido")
        Integer ano,

        @NotNull(message = "O ID do cliente (proprietário) é obrigatório")
        Long idCliente
) {

}