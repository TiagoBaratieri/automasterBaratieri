package com.baratieri.automasterbaratieri.dto.response;


import com.baratieri.automasterbaratieri.entities.Peca;

import java.math.BigDecimal;

public record PecaResponseDTO(

        String sku,
        String nome,
        String partNumber,
        BigDecimal precoVenda,
        BigDecimal precoCusto,
        Integer quantidadeEstoque,
        Integer estoqueMinimo,
        String nomeCompleto) {

    public static PecaResponseDTO fromEntity(Peca peca) {
        if (peca == null) return null;
        String nomeJunto = peca.getNome() + " " + peca.getMarca() + " - " + peca.getAplicacao();
        return new PecaResponseDTO(peca.getSku(),
                peca.getNome(),
                peca.getPartNumber(),
                peca.getPrecoVenda(),
                peca.getPrecoCusto(),
                peca.getQuantidadeEstoque(),
                peca.getEstoqueMinimo(),
                nomeJunto);
    }
}
