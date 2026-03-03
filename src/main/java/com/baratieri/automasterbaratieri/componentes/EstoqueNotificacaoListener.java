package com.baratieri.automasterbaratieri.componentes;

import com.baratieri.automasterbaratieri.eventos.EstoqueBaixoEvento;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EstoqueNotificacaoListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processarEstoqueBaixo(EstoqueBaixoEvento evento) {
        System.out.println("==================================================");
        System.out.println("🚨 ALERTA DE ESTOQUE MÍNIMO ATINGIDO 🚨");
        System.out.println("Peça: " + evento.nomePeca() + " (ID: " + evento.pecaId() + ")");
        System.out.println("Quantidade atual: " + evento.quantidadeAtual());
        System.out.println("Estoque mínimo exigido: " + evento.estoqueMinimo());
        System.out.println("Ação: Gerente de compras precisa ser notificado.");
        System.out.println("==================================================");
    }
}
