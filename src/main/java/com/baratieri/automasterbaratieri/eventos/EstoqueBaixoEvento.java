package com.baratieri.automasterbaratieri.eventos;


import com.baratieri.automasterbaratieri.entities.Peca;

public record EstoqueBaixoEvento(Long pecaId,
                                 String nomePeca,
                                 String sku,
                                 Integer quantidadeAtual,
                                 Integer estoqueMinimo) {


    public static EstoqueBaixoEvento fromEntity(Peca peca) {
        return new EstoqueBaixoEvento(
                peca.getId(),
                peca.getNome(),
                peca.getSku(),
                peca.getQuantidadeEstoque(),
                peca.getEstoqueMinimo()
        );
    }

}
