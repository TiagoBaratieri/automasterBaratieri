package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.services.interfaces.GeradorPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final OrdemServicoService ordemServicoService;
    private final GeradorPdfService geradorPdf;

    public byte[] gerarPdfOrdemServico(Long id) {
        OrdemServico os = ordemServicoService.ordemServicoExiste(id);

        return geradorPdf.gerarPdfOrdemServico(os);
    }

    public byte[] gerarReciboPagamento(Long osId) {

        OrdemServico os = ordemServicoService.ordemServicoExiste(osId);

        os.validarGeracaoRecibo();

        BigDecimal totalPago = os.calcularTotalPago();

        return geradorPdf.gerarReciboPagamento(os, totalPago);
    }
}