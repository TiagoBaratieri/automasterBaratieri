package com.baratieri.automasterbaratieri.dto.request;


import com.baratieri.automasterbaratieri.validation.Placa;
import jakarta.validation.constraints.Size;

public record AberturaOsRequestDTO(
        @Placa
        String placaVeiculo,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacaoInicial

) {}