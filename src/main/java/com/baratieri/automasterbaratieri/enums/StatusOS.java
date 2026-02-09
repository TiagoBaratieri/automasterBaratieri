package com.baratieri.automasterbaratieri.enums;


public enum StatusOS {
    // 1. O.S. criada, aguardando cliente autorizar
    ORCAMENTO,

    // 2. Cliente autorizou, peças reservadas no estoque
    APROVADO,

    // 3. Mecânico iniciou o trabalho
    EM_EXECUCAO,

    // 4. Carro pronto, aguardando pagamento/retirada
    FINALIZADO,

    // 5. Cliente não aceitou o orçamento
    CANCELADO;

    public boolean permiteEdicao() {
        return this == ORCAMENTO || this == APROVADO || this == EM_EXECUCAO;
    }
}