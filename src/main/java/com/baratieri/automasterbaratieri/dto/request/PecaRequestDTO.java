package com.baratieri.automasterbaratieri.dto.request;


import com.baratieri.automasterbaratieri.entities.Peca;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PecaRequestDTO(

        @NotBlank(message = "O nome da peça é obrigatório")
        String nome,

        @NotBlank(message = "O código SKU é obrigatório")
        String sku, // Código único da peça (ex: OLE-5W30)

        @NotNull(message = "O preço de venda é obrigatório")
        @Positive(message = "O preço de venda deve ser maior que zero")
        BigDecimal precoVenda,

        @NotNull(message = "A quantidade em estoque é obrigatória")
        @PositiveOrZero(message = "O estoque não pode ser negativo")
        Integer quantidadeEstoque
) {
        public Peca toEntity() {
                Peca peca = new Peca();
                peca.setNome(this.nome);
                peca.setSku(this.sku);
                peca.setPrecoVenda(this.precoVenda);
                peca.setQuantidadeEstoque(this.quantidadeEstoque);
                return peca;
        }
}