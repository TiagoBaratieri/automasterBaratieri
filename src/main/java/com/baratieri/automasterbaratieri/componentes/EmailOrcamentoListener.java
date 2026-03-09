package com.baratieri.automasterbaratieri.componentes;

import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.eventos.OrcamentoProntoEvento;
import com.baratieri.automasterbaratieri.services.RelatorioService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailOrcamentoListener {

    private final JavaMailSender mailSender;

    private final RelatorioService relatorioService;

    @Value("${automaster.notificacao.estoque.remetente}")
    private String emailRemetente;

    @Async
    @EventListener
    public void enviarPdfAoCliente(OrcamentoProntoEvento evento) {
        try {
            OrdemServico os = evento.os();

            byte[] pdf = relatorioService.gerarPdfOrdemServico(os.getId());

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = getMimeMessageHelper(message, os);

            helper.addAttachment("Ordem_Servico_" + os.getId() + ".pdf", new ByteArrayResource(pdf));

            mailSender.send(message);
            System.out.println("Ficheiro PDF da O.S. enviado com sucesso para o cliente!");

        } catch (Exception e) {

            System.err.println("ERRO DE INFRAESTRUTURA: O orçamento foi salvo, mas o e-mail falhou. Motivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private @NonNull MimeMessageHelper getMimeMessageHelper(MimeMessage message, OrdemServico os) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(emailRemetente);

        helper.setTo(os.getVeiculo().getCliente().getEmail());

        helper.setSubject("Orçamento para Avaliação - AutoMaster - OS Nº " + os.getId());
        helper.setText("Olá, " + os.getVeiculo().getCliente().getNome() + "!\n\n" +
                "O orçamento para a manutenção do seu veículo já está pronto. " +
                "Confira o PDF em anexo com a lista de peças, serviços e o valor total.\n\n" +
                "Por favor, responda a este e-mail aprovando o serviço para iniciarmos os trabalhos.");
        return helper;
    }
}
