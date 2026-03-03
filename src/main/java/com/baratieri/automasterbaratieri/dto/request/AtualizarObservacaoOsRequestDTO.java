package com.baratieri.automasterbaratieri.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AtualizarObservacaoOsRequestDTO(
        @NotBlank(message = "A observação não pode estar vazia.")
                                              String observacao) {
}
