package com.baratieri.automasterbaratieri.services;


import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Service
public class RelatorioService {

    private final TemplateEngine templateEngine;
    private final OrdemServicoService ordemServicoService;

    public RelatorioService(TemplateEngine templateEngine, OrdemServicoService ordemServicoService) {
        this.templateEngine = templateEngine;
        this.ordemServicoService = ordemServicoService;
    }

    public byte[] gerarPdfOrdemServico(Long id) {
        OrdemServico os = ordemServicoService.ordemServicoExiste(id);

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

            throw new RegraNegocioException("Erro ao gerar o documento PDF da Ordem de Serviço");
        }
    }
}

