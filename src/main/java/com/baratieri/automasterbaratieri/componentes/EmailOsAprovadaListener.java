package com.baratieri.automasterbaratieri.componentes;

import com.baratieri.automasterbaratieri.services.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.eventos.OrdemServicoAprovadaEvento;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class EmailOsAprovadaListener {

    private final JavaMailSender mailSender;

    private final RelatorioService relatorioService;

    @Value("${spring.mail.username}")
    private String emailDestinatario;

    @Async
    @EventListener
    public void enviarPdfAoCliente(OrdemServicoAprovadaEvento evento) {
        try {
            OrdemServico os = evento.os();

            byte[] pdf = relatorioService.gerarPdfOrdemServico(os.getId());

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailDestinatario);

            helper.setTo(os.getVeiculo().getCliente().getEmail());

            helper.setSubject("Orçamento Aprovado - AutoMaster - OS Nº " + os.getId());
            helper.setText("Olá, " + os.getVeiculo().getCliente().getNome() + "!\n\n" +
                    "O orçamento da sua viatura foi aprovado com sucesso. " +
                    "Segue em anexo o documento detalhado em PDF com as peças e serviços.\n\n" +
                    "Cumprimentos,\nEquipe AutoMaster");

            helper.addAttachment("Ordem_Servico_" + os.getId() + ".pdf", new ByteArrayResource(pdf));

            mailSender.send(message);
            System.out.println("Ficheiro PDF da O.S. enviado com sucesso para o cliente!");

        } catch (Exception e) {
            System.err.println("Erro ao enviar o PDF aprovado por e-mail: " + e.getMessage());
        }
    }
}
