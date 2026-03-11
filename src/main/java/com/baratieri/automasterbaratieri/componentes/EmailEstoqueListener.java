package com.baratieri.automasterbaratieri.componentes;

import com.baratieri.automasterbaratieri.eventos.EstoqueBaixoEvento;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener; // <-- Import necessário!
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailEstoqueListener {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailRemetente;

    @Value("${spring.mail.username}")
    private String emailDestinatario;

    @Async
    @EventListener
    public void notificarPorEmail(EstoqueBaixoEvento evento){
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(emailRemetente);
        mensagem.setTo(emailDestinatario);
        mensagem.setSubject("⚠️ ALERTA: Estoque Baixo - " + evento.nomePeca());

        mensagem.setText("A peça " + evento.nomePeca() + " (SKU: " + evento.sku() +
                ") atingiu o estoque mínimo. Restam apenas " +
                evento.quantidadeAtual() + " unidades. ");

        mailSender.send(mensagem);
        System.out.println("E-mail de estoque baixo enviado com sucesso!");
    }
}