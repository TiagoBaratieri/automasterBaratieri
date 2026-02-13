package com.baratieri.automasterbaratieri.dto.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record PecaRequestDTO(

        @NotBlank(message = "O SKU é obrigatório")
        @Size(min = 3, max = 20, message = "O SKU deve ter entre 3 e 20 caracteres")
        String sku,

        @NotBlank(message = "O nome da peça é obrigatório")
        String nome,

        @NotBlank(message = "O Part Number (código do fabricante) é obrigatório")
        String partNumber,

        @NotNull(message = "O preço de venda é obrigatório")
        @Positive(message = "O preço de venda deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "Formato de preço inválido (máximo 10 dígitos e 2 casas decimais)")
        BigDecimal precoVenda,

        @NotNull(message = "O preço de custo é obrigatório")
        @PositiveOrZero(message = "O preço de custo não pode ser negativo")
        BigDecimal precoCusto,

        @NotNull(message = "A quantidade inicial é obrigatória")
        @Min(value = 0, message = "O estoque não pode ser negativo")
        Integer quantidadeEstoque,

        @NotNull(message = "O estoque mínimo é obrigatório")
        @Min(value = 0, message = "O estoque mínimo não pode ser menor que 5")
        Integer estoqueMinimo
) {}