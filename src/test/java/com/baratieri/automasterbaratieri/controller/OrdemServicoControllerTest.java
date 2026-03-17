package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.AberturaOsRequestDTO;
import com.baratieri.automasterbaratieri.dto.request.PecaPayloadDTO;
import com.baratieri.automasterbaratieri.dto.response.OrdemServicoResponseDTO;
import com.baratieri.automasterbaratieri.services.OrdemServicoService;
import com.baratieri.automasterbaratieri.services.RelatorioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdemServicoController.class)
class OrdemServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrdemServicoService ordemServicoService;

    @MockBean
    private RelatorioService relatorioService;

    @Test
    void deveRetornarPdfComHeadersCorretosAoBaixarRelatorio() throws Exception {
        Long osId = 1L;
        byte[] pdfContent = "fake pdf content".getBytes();
        when(relatorioService.gerarPdfOrdemServico(osId)).thenReturn(pdfContent);

        mockMvc.perform(get("/ordens-servico/{id}/relatorio", osId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Ordem_Servico_1.pdf\""))
                .andExpect(content().bytes(pdfContent));

        verify(relatorioService).gerarPdfOrdemServico(osId);
    }

    @Test
    void deveRetornarCreatedEHeaderLocationAoAbrirOs() throws Exception {
        // Arrange
        AberturaOsRequestDTO requestDTO = new AberturaOsRequestDTO("ABC-1234", "Observação inicial");
        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(
                1L, "OS-1", LocalDateTime.now(), null, null, null, null, null, Collections.emptyList(), Collections.emptyList(), BigDecimal.ZERO
        );

        when(ordemServicoService.abrirOdemServico(any(AberturaOsRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/ordens-servico/1"));

        verify(ordemServicoService).abrirOdemServico(any(AberturaOsRequestDTO.class));
    }

    @Test
    void deveRetornarErroDeValidacaoQuandoPayloadPecaForInvalido() throws Exception {
        Long osId = 1L;
        PecaPayloadDTO invalidPayload = new PecaPayloadDTO(null, null, null);

        mockMvc.perform(post("/ordens-servico/{id}/pecas", osId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isUnprocessableEntity());

        verify(ordemServicoService, never()).adicionarPecaOrdemServico(anyLong(), any(PecaPayloadDTO.class));
    }
}