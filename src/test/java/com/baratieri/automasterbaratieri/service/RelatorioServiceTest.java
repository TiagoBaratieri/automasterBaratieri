package com.baratieri.automasterbaratieri.service;

import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.services.OrdemServicoService;
import com.baratieri.automasterbaratieri.services.RelatorioService;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @InjectMocks
    private RelatorioService relatorioService;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private OrdemServicoService ordemServicoService;

    private final Long osId = 1L;

    @Test
    @DisplayName("Deve gerar o ficheiro PDF da Ordem de Serviço com sucesso")
    void deveGerarPdfComSucesso() {
        OrdemServico osFalsa = fabricaOrdemServicoFalsa();
        // Simula o HTML que o Thymeleaf iria gerar
        String htmlFalso = "<html><body><h1>Recibo da Oficina</h1></body></html>";

        when(ordemServicoService.ordemServicoExiste(osId)).thenReturn(osFalsa);

        // Quando o serviço pedir ao Thymeleaf para processar, devolvemos a nossa string
        when(templateEngine.process(eq("ordem-servico"), any(Context.class))).thenReturn(htmlFalso);

        byte[] pdfBytes = relatorioService.gerarPdfOrdemServico(osId);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0, "O array de bytes do PDF não deveria estar vazio");


        verify(ordemServicoService, times(1)).ordemServicoExiste(osId);
        verify(templateEngine, times(1)).process(eq("ordem-servico"), any(Context.class));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando a geração do PDF falhar")
    void deveLancarExcecaoAoFalharGeracaoPdf() {

        OrdemServico osFalsa = fabricaOrdemServicoFalsa();

        when(ordemServicoService.ordemServicoExiste(osId)).thenReturn(osFalsa);

        // Simula uma falha catastrófica no motor de templates (HTML inválido, por exemplo)
        when(templateEngine.process(eq("ordem-servico"), any(Context.class)))
                .thenThrow(new RuntimeException("Erro interno no motor HTML"));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            relatorioService.gerarPdfOrdemServico(osId);
        });

        assertEquals("Erro ao gerar o documento PDF da Ordem de Serviço", exception.getMessage());
    }

    private OrdemServico fabricaOrdemServicoFalsa() {
        OrdemServico os = new OrdemServico();
        os.setId(osId);
        return os;
    }
}