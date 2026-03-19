package com.baratieri.automasterbaratieri.infraestrutura.pdf;

import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.interfaces.GeradorPdfService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OpenHtmlGeradorPdfImpl implements GeradorPdfService {

    private final TemplateEngine templateEngine;

    @Override
    public byte[] gerarPdfOrdemServico(OrdemServico os) {
        Context context = new Context();
        context.setVariable("os", os);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String html = templateEngine.process("ordem-servico", context);

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            // Em infraestrutura, capturamos o erro técnico e lançamos o erro de negócio
            throw new RegraNegocioException("Erro interno ao gerar o documento PDF da Ordem de Serviço");
        }
    }

    @Override
    public byte[] gerarReciboPagamento(OrdemServico os, BigDecimal totalPago) {
        Context context = new Context();
        context.setVariable("os", os);
        context.setVariable("totalPago", totalPago); // Injetamos o cálculo feito pelo Domínio!

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String html = templateEngine.process("recibo-pagamento", context);

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RegraNegocioException("Erro interno ao gerar o recibo de pagamento em PDF");
        }
    }
}