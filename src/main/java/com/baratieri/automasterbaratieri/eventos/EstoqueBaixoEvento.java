package com.baratieri.automasterbaratieri.eventos;


import com.baratieri.automasterbaratieri.entities.Peca;

public record EstoqueBaixoEvento(Long pecaId,
                                 String nomePeca,
                                 Integer quantidadeAtual,
                                 Integer estoqueMinimo) {


    public static EstoqueBaixoEvento fromEntity(Peca peca) {
        return new EstoqueBaixoEvento(
                peca.getId(),
                peca.getNome(),
                peca.getQuantidadeEstoque(),
                peca.getEstoqueMinimo()
        );
    }

}
