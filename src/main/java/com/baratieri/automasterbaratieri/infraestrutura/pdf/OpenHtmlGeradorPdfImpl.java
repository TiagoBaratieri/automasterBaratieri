package com.baratieri.automasterbaratieri.infraestrutura.pdf;
import com.baratieri.automasterbaratieri.dto.response.PagamentoResponseDTO;
import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.entities.Pagamento;
import com.baratieri.automasterbaratieri.entities.PagamentoCartao;
import com.baratieri.automasterbaratieri.entities.PagamentoDinheiro;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.interfaces.GeradorPdfService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OpenHtmlGeradorPdfImpl implements GeradorPdfService {

    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public byte[] gerarPdfOrdemServico(OrdemServico os) {
        Context context = new Context();
        context.setVariable("os", os);
        context.setVariable("baseUrl", baseUrl);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String html = templateEngine.process("ordem-servico", context);

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RegraNegocioException("Erro interno ao gerar o documento PDF da Ordem de Serviço");
        }
    }

    @Override
    public byte[] gerarReciboPagamento(OrdemServico os, BigDecimal totalPago) {
        List<PagamentoResponseDTO> pagamentosDTO = os.getPagamentos().stream()
                .map(this::toPagamentoResponseDTO)
                .collect(Collectors.toList());

        Context context = new Context();
        context.setVariable("os", os);
        context.setVariable("totalPago", totalPago);
        context.setVariable("pagamentosDTO", pagamentosDTO);

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

    private PagamentoResponseDTO toPagamentoResponseDTO(Pagamento pagamento) {
        String tipoPagamento;
        String detalhes = "N/A";
        BigDecimal valorRecebido = null;
        BigDecimal troco = null;

        if (pagamento instanceof PagamentoCartao cartao) {
            tipoPagamento = "Cartão";
            detalhes = cartao.getNumeroParcelas() + "x de R$ " + (cartao.getValor().divide(BigDecimal.valueOf(cartao.getNumeroParcelas())));
        } else if (pagamento instanceof PagamentoDinheiro dinheiro) {
            tipoPagamento = "Dinheiro";
            detalhes = "Pagamento à vista";
            valorRecebido = dinheiro.getValorRecebido();
            troco = dinheiro.getTroco();
        } else {
            tipoPagamento = "Pix";
        }

        return new PagamentoResponseDTO(
                pagamento.getId(),
                pagamento.getValor(),
                pagamento.getStatusPagamento(),
                pagamento.getDataPagamento(),
                tipoPagamento,
                detalhes,
                valorRecebido,
                troco
        );
    }
}
