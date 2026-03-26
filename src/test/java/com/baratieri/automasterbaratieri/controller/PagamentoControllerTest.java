package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.response.PagamentoResponseDTO;
import com.baratieri.automasterbaratieri.enums.StatusPagamento;
import com.baratieri.automasterbaratieri.services.PagamentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagamentoController.class)
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagamentoService pagamentoService;

    @Test
    @DisplayName("Deve retornar 201 Created quando registrar pagamento com sucesso")
    void deveRetornarCreatedQuandoRegistrarPagamento() throws Exception {

        when(pagamentoService.registrarPagamento(anyLong(), any()))
                .thenReturn(new PagamentoResponseDTO(1L, new BigDecimal("150.00"), StatusPagamento.PAGO, LocalDateTime.now(),
                        null,null,null,null));

        String jsonPayload =jsonPayloadPagamentotoComSucesso();

        mockMvc.perform(post("/ordens-servico/1/pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor").value(150.00)) // Garante que o valor retornou certo
                .andExpect(jsonPath("$.status").value("PAGO")); // Garante que o status mudou
    }

    @Test
    @DisplayName("Deve retornar 422 Unprocessable Entity quando tipoPagamento estiver em branco")
    void deveRetornarUnprocessableEntityQuandoTipoPagamentoEmBranco() throws Exception {

        String jsonPayload = jsonPayloadTipoPagamentoEmBranco();

        mockMvc.perform(post("/ordens-servico/1/pagamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isUnprocessableEntity());
    }

    private String jsonPayloadPagamentotoComSucesso() {
        return """
            {
                "tipoPagamento": "PIX",
                "valor": 150.00,
                "chavePix": "123456789"
            }
            """;
    }

    private String jsonPayloadTipoPagamentoEmBranco () {
        return """
            {
                "tipoPagamento": "",
                "valor": 150.00
            }
            """;
    }
}
