package com.baratieri.automasterbaratieri.service;

import com.baratieri.automasterbaratieri.dto.request.PagamentoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PagamentoResponseDTO;
import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.entities.Pagamento;
import com.baratieri.automasterbaratieri.enums.StatusOS;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PagamentoServiceTeste {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private OrdemServicoService ordemServicoService;

    @InjectMocks
    private PagamentoService pagamentoService;

    @Test
    @DisplayName("Deve registrar pagamento e salvar no banco de dados com sucesso")
    void deveRegistrarPagamnetoComSucesso() {
        Long osId = 1L;
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setId(osId);
        ordemServico.setStatus(StatusOS.FINALIZADO);
        ordemServico.setValorTotal(new BigDecimal("1000.00"));

        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO("PIX",
                new BigDecimal("500.00"), null, null, "123", null);

        when(ordemServicoService.ordemServicoExiste(osId)).thenReturn(ordemServico);

        when(pagamentoRepository.save(any())).thenAnswer(invocation ->
                invocation.getArgument(0));

        PagamentoResponseDTO responseDTO = pagamentoService.registrarPagamento(osId, requestDTO);

        assertNotNull(responseDTO);
        assertEquals(new BigDecimal("500.00"), responseDTO.valor());
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve lançar Exceção quando a O.S. Não estiver finalizada")
    void deveLacarExceptionQuandoestiverFinalizado() {
        Long osId = 1L;
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setId(osId);
        ordemServico.setStatus(StatusOS.AGUARDANDO_APROVACAO);

        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO("PIX",
                new BigDecimal("500.00"), null, null, "123", null);

        when(ordemServicoService.ordemServicoExiste(osId)).thenReturn(ordemServico);

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            pagamentoService.registrarPagamento(osId, requestDTO);
        });

        verify(pagamentoRepository, never()).save(any());

    }
}