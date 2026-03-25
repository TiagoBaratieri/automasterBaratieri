package com.baratieri.automasterbaratieri.componentes;

import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.eventos.PagamentoRegistradoEvento;
import com.baratieri.automasterbaratieri.services.OrdemServicoService;
import com.baratieri.automasterbaratieri.services.RelatorioService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Importante!

@Component
@RequiredArgsConstructor
public class EmailReciboPagamentoListener {

    private final JavaMailSender mailSender;
    private final RelatorioService relatorioService;
    private final OrdemServicoService ordemServicoService;

    @Value("${spring.mail.username}")
    private String emailRemetente;

    @Async
    @EventListener
    @Transactional(readOnly = true)
    public void enviarReciboPagamentoCliente(PagamentoRegistradoEvento evento) {
        try {
            OrdemServico ordemServico = ordemServicoService.ordemServicoExiste(evento.id());


            if (ordemServico.getStatus() != StatusOS.PAGO) {
                System.out.println("Pagamento parcial registado para a OS " + ordemServico.getId() + "." +
                        " E-mail em espera até liquidação total.");
                return;
            }

            byte[] pdfRecibo = relatorioService.gerarReciboPagamento(ordemServico.getId());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = getMimeMessageHelper(message, ordemServico);

            helper.addAttachment("Recibo_Pagamento_OS_" + ordemServico.getId() + ".pdf", new ByteArrayResource(pdfRecibo));
            mailSender.send(message);

            System.out.println("Recibo PDF enviado com sucesso para o cliente da OS: " + ordemServico.getId());

        } catch (Exception e) {
            System.err.println("Erro ao enviar o Recibo por e-mail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private MimeMessageHelper getMimeMessageHelper(MimeMessage message, OrdemServico os) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(emailRemetente);
        helper.setTo(os.getVeiculo().getCliente().getEmail());

        String assunto = assuntoRecibo(os);
        String textoBase = textoBaseRecibo(os);


        helper.setSubject(assunto);
        helper.setText(textoBase);

        return helper;
    }

    private String textoBaseRecibo(OrdemServico os) {
        return "Olá, " + os.getVeiculo().getCliente().getNome() + "!\n\n" +
                "O seu pagamento foi processado com sucesso. " +
                "Segue em anexo o recibo detalhado em PDF.\n\n" +
                "Obrigado por escolher a AutoMaster!";
    }

    private String assuntoRecibo(OrdemServico os) {
        return "Recibo de Pagamento - AutoMaster - OS Nº " + os.getId();
    }
}