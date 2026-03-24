package com.baratieri.automasterbaratieri.service;

import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.services.OrdemServicoService;
import com.baratieri.automasterbaratieri.services.RelatorioService;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.interfaces.GeradorPdfService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @InjectMocks
    private RelatorioService relatorioService;

    @Mock
    private GeradorPdfService geradorPdf;

    @Mock
    private OrdemServicoService ordemServicoService;

    private final Long osId = 1L;

    @Test
    @DisplayName("Deve gerar o ficheiro PDF da Ordem de Serviço com sucesso")
    void deveGerarPdfComSucesso() {
        OrdemServico osFalsa = fabricaOrdemServicoFalsa();
        byte[] pdfFalso = "Fake PDF".getBytes();

        when(ordemServicoService.ordemServicoExiste(osId)).thenReturn(osFalsa);
        when(geradorPdf.gerarPdfOrdemServico(any(OrdemServico.class))).thenReturn(pdfFalso);

        byte[] pdfBytes = relatorioService.gerarPdfOrdemServico(osId);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0, "O array de bytes do PDF não deveria estar vazio");
        assertEquals(pdfFalso, pdfBytes);

        verify(ordemServicoService, times(1)).ordemServicoExiste(osId);
        verify(geradorPdf, times(1)).gerarPdfOrdemServico(osFalsa);
    }

    @Test
    @DisplayName("Deve relançar exceção quando o gerador de PDF falhar")
    void deveLancarExcecaoAoFalharGeracaoPdf() {
        OrdemServico osFalsa = fabricaOrdemServicoFalsa();

        when(ordemServicoService.ordemServicoExiste(osId)).thenReturn(osFalsa);
        when(geradorPdf.gerarPdfOrdemServico(any(OrdemServico.class)))
                .thenThrow(new RegraNegocioException("Erro interno ao gerar o documento"));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            relatorioService.gerarPdfOrdemServico(osId);
        });

        assertEquals("Erro interno ao gerar o documento", exception.getMessage());
    }

    private OrdemServico fabricaOrdemServicoFalsa() {
        OrdemServico os = new OrdemServico();
        os.setId(osId);
        return os;
    }
}
