package com.baratieri.automasterbaratieri.enums;


import java.util.List;

public enum StatusOS {
    // 1. O.S. criada, aguardando cliente autorizar
    AGUARDANDO_APROVACAO,

    // 2. Cliente autorizou, peças reservadas no estoque
    APROVADO,

    // 3. Mecânico iniciou o trabalho
    EM_EXECUCAO,

    // 4. Carro pronto, aguardando pagamento/retirada
    FINALIZADO,

    // 5. Cliente não aceitou o orçamento
    CANCELADO,

    PAGO;

    public static List<StatusOS> getAtivos() {
        return List.of(APROVADO,AGUARDANDO_APROVACAO, EM_EXECUCAO);
    }
}