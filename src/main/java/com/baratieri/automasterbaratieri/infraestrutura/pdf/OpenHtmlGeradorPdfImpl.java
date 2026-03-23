package com.baratieri.automasterbaratieri.infraestrutura.pdf;
import com.baratieri.automasterbaratieri.dto.response. ReciboPagamentoResponseDTO;
import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.entities.Pagamento;
import com.baratieri.automasterbaratieri.entities.PagamentoCartao;
import com.baratieri.automasterbaratieri.entities.PagamentoDinheiro;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.interfaces.GeradorPdfService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
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
            throw new RegraNegocioException("Erro interno ao gerar o documento PDF da Ordem de Serviço");
        }
    }

    @Override
    public byte[] gerarReciboPagamento(OrdemServico os, BigDecimal totalPago) {
        List<ReciboPagamentoResponseDTO> responseDTO = os.getPagamentos().stream()
                .map(this::toReciboPagamentoDTO)
                .collect(Collectors.toList());

        Context context = new Context();
        context.setVariable("os", os);
        context.setVariable("totalPago", totalPago);
        context.setVariable("pagamentosDTO", responseDTO); // Injeta a lista de DTOs

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

    private ReciboPagamentoResponseDTO toReciboPagamentoDTO(Pagamento pagamento) {
        String tipoPagamento;
        String detalhes;

        if (pagamento instanceof PagamentoCartao cartao) {
            tipoPagamento = "Cartão";
            detalhes = cartao.getNumeroParcelas() + "x de R$ " + (cartao.getValor().divide(BigDecimal.valueOf(cartao.getNumeroParcelas())));
        } else if (pagamento instanceof PagamentoDinheiro) {
            tipoPagamento = "Dinheiro";
            detalhes = "Pagamento à vista";
        } else {
            tipoPagamento = "Pix";
            detalhes = "N/A";
        }

        return new  ReciboPagamentoResponseDTO(
                tipoPagamento,
                pagamento.getValor(),
                detalhes,
                pagamento.getDataPagamento()
        );
    }
}
