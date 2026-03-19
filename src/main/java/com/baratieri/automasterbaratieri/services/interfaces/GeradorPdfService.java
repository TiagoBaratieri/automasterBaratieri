package com.baratieri.automasterbaratieri.services.interfaces;
import com.baratieri.automasterbaratieri.entities.OrdemServico;
import java.math.BigDecimal;

public interface GeradorPdfService {
    byte[] gerarPdfOrdemServico(OrdemServico os);
    byte[] gerarReciboPagamento(OrdemServico os, BigDecimal totalPago);
}