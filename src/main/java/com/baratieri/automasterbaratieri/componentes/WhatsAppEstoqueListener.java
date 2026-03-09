package com.baratieri.automasterbaratieri.componentes;

import com.baratieri.automasterbaratieri.eventos.EstoqueBaixoEvento;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WhatsAppEstoqueListener {

    @EventListener
    public void notificarPorWhatsapp(EstoqueBaixoEvento evento) {
        String mensagemZAP = "⚠️ *AutoMaster Alerta* \n" +
                "Peça acabando: " + evento.nomePeca() + "\n" +
                "Estoque atual: " + evento.quantidadeAtual();

        // Aqui você faria uma chamada HTTP (usando RestTemplate ou OpenFeign)
        // para a API de WhatsApp que você escolheu contratar/usar.

        // Exemplo fictício chamando uma API:
        // whatsappApiClient.enviarMensagem("5544999999999", mensagemZAP);

        System.out.println("Notificação de WhatsApp disparada na fila!");
    }
}