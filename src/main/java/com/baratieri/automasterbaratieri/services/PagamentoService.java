package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.PagamentoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PagamentoResponseDTO;
import com.baratieri.automasterbaratieri.entities.*;
import com.baratieri.automasterbaratieri.eventos.PagamentoRegistradoEvento;
import com.baratieri.automasterbaratieri.repositories.PagamentoRepository;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final OrdemServicoService ordemServicoService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PagamentoResponseDTO registrarPagamento(Long osId, PagamentoRequestDTO dto) {
        OrdemServico os = ordemServicoService.ordemServicoExiste(osId);

        os.validarPagamentoApenasOrdemServicoFinalizada();
        Pagamento pagamento = fabricarPagamento(dto, os);
        os.adicionarPagamento(pagamento);
        pagamento.confirmarPagamento();
        eventPublisher.publishEvent(new PagamentoRegistradoEvento(this, os.getId()));
        pagamentoRepository.save(pagamento);

        return PagamentoResponseDTO.fromEntity(pagamento);
    }

    private Pagamento fabricarPagamento(PagamentoRequestDTO dto, OrdemServico os) {
        return switch (dto.tipoPagamento().toUpperCase()) {
            case "PIX" -> new PagamentoPix(os, dto.valor(), dto.chavePix());
            case "CARTAO" -> new PagamentoCartao(os, dto.valor(), dto.parcelas(), dto.bandeira());
            case "DINHEIRO" -> new PagamentoDinheiro(os, dto.valor(), dto.valorRecebido());
            default -> throw new RegraNegocioException("Tipo de pagamento inválido. Use PIX, CARTAO ou DINHEIRO.");
        };
    }
}
