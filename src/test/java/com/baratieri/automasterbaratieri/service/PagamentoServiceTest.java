package com.baratieri.automasterbaratieri.service;

import com.baratieri.automasterbaratieri.dto.request.PagamentoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PagamentoResponseDTO;
import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.entities.Pagamento;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.eventos.PagamentoRegistradoEvento;
import com.baratieri.automasterbaratieri.repositories.PagamentoRepository;
import com.baratieri.automasterbaratieri.services.OrdemServicoService;
import com.baratieri.automasterbaratieri.services.PagamentoService;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private OrdemServicoService ordemServicoService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PagamentoService pagamentoService;

    // Constantes imutáveis da classe
    private final BigDecimal valorPagamento = new BigDecimal("500.00");
    private final Long osId = 1L;

    @Test
    @DisplayName("Deve registrar pagamento e salvar no banco de dados com sucesso")
    void deveRegistrarPagamentoComSucesso() {

        PagamentoRequestDTO requestDTO = fabricaPagamentoRequestDTO(valorPagamento);
        when(ordemServicoService.ordemServicoExiste(osId)).thenReturn(fabricaOrdemServico(StatusOS.FINALIZADO));
        when(pagamentoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(eventPublisher).publishEvent(any(PagamentoRegistradoEvento.class)); // Correção aqui

        PagamentoResponseDTO responseDTO = pagamentoService.registrarPagamento(osId, requestDTO);

        assertNotNull(responseDTO);
        assertEquals(valorPagamento, responseDTO.valor());
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
        verify(eventPublisher, times(1)).publishEvent(any(PagamentoRegistradoEvento.class)); // Correção aqui
    }

    @Test
    @DisplayName("Deve lançar Exceção quando a O.S. NÃO estiver finalizada")
    void deveLancarExcecaoQuandoOsNaoEstiverFinalizada() {
        PagamentoRequestDTO requestDTO = fabricaPagamentoRequestDTO(valorPagamento);
        when(ordemServicoService.ordemServicoExiste(osId)).thenReturn(fabricaOrdemServico(StatusOS.AGUARDANDO_APROVACAO));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            pagamentoService.registrarPagamento(osId, requestDTO);
        });

        verify(pagamentoRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any(PagamentoRegistradoEvento.class)); // Correção aqui
    }

    private PagamentoRequestDTO fabricaPagamentoRequestDTO(BigDecimal valorPagamento) {
        return new PagamentoRequestDTO("PIX", valorPagamento, null, null, "123", null);
    }

    private OrdemServico fabricaOrdemServico(StatusOS status) {
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setId(osId);
        ordemServico.setStatus(status);
        ordemServico.setValorTotal(new BigDecimal("1000.00"));
        return ordemServico;
    }
}
