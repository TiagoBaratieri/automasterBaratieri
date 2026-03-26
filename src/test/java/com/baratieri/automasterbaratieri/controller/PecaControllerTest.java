package com.baratieri.automasterbaratieri.controller;


import com.baratieri.automasterbaratieri.dto.request.AtualizarPecaRequestDTO;
import com.baratieri.automasterbaratieri.dto.request.PecaRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PecaResponseDTO;
import com.baratieri.automasterbaratieri.services.PecaService;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PecaController.class)
class PecaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PecaService pecaService;

    // =========================================================================
    // GET /pecas/{id}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com o DTO correto ao buscar peça por ID existente")
    void deveRetornar200ComDtoCorretoAoBuscarPecaPorId() throws Exception {
        // ARRANGE
        when(pecaService.buscarPecaPorId(1L)).thenReturn(fabricarPecaResponseDTO());

        // ACT & ASSERT
        mockMvc.perform(get("/pecas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU123"))
                .andExpect(jsonPath("$.nome").value("FILTRO DE ÓLEO"))
                .andExpect(jsonPath("$.precoVenda").value(150.00));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando peça não for encontrada pelo ID")
    void deveRetornar404QuandoPecaNaoForEncontradaPeloId() throws Exception {
        // ARRANGE
        when(pecaService.buscarPecaPorId(999L))
                .thenThrow(new ResourceNotFoundException("Peça não encontrada com ID: 999"));

        // ACT & ASSERT
        mockMvc.perform(get("/pecas/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Peça não encontrada com ID: 999"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o ID for um texto não-numérico")
    void deveRetornar400QuandoIdForTextoInvalido() throws Exception {
        // ACT & ASSERT — MethodArgumentTypeMismatchException tratada pelo ResourceExceptionHandler
        mockMvc.perform(get("/pecas/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Parâmetro de URL inválido"));
    }

    // =========================================================================
    // GET /pecas
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com Page de DTOs ao pesquisar estoque com filtros")
    void deveRetornar200ComPaginaDeResultadosAoPesquisarEstoque() throws Exception {
        // ARRANGE
        PageImpl<PecaResponseDTO> paginaFake = new PageImpl<>(
                List.of(fabricarPecaResponseDTO()),
                PageRequest.of(0, 10),
                1
        );
        when(pecaService.buscarPecas(any(), any(), any(), any())).thenReturn(paginaFake);

        // ACT & ASSERT
        mockMvc.perform(get("/pecas")
                        .param("nome", "filtro")
                        .param("marca", "Bosch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].sku").value("SKU123"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // =========================================================================
    // POST /pecas
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 201 Created com header Location ao salvar peça com payload válido")
    void deveRetornar201ComLocationAoSalvarPecaValida() throws Exception {
        // ARRANGE
        when(pecaService.salvarPeca(any(PecaRequestDTO.class))).thenReturn(fabricarPecaResponseDTO());

        // ACT & ASSERT
        mockMvc.perform(post("/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayloadPecaValida()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.sku").value("SKU123"))
                .andExpect(jsonPath("$.precoVenda").value(150.00));

        verify(pecaService, times(1)).salvarPeca(any(PecaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 422 Unprocessable Entity quando o payload POST estiver vazio (Bean Validation)")
    void deveRetornar422QuandoPayloadDoPostForInvalido() throws Exception {
        // ACT & ASSERT — "{}" dispara todos os @NotBlank e @NotNull do PecaRequestDTO
        mockMvc.perform(post("/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Erro de Validação"));

        // Garante que o serviço não é acionado quando a validação falha
        verify(pecaService, never()).salvarPeca(any());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o serviço rejeitar SKU duplicado")
    void deveRetornar400QuandoSkuJaEstiverCadastrado() throws Exception {
        // ARRANGE
        when(pecaService.salvarPeca(any(PecaRequestDTO.class)))
                .thenThrow(new RegraNegocioException("Já existe uma peça cadastrada com o SKU: SKU123"));

        // ACT & ASSERT
        mockMvc.perform(post("/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayloadPecaValida()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Já existe uma peça cadastrada com o SKU: SKU123"));
    }

    // =========================================================================
    // PATCH /pecas/{id}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com preços atualizados ao fazer PATCH em peça existente")
    void deveRetornar200ComDadosAtualizadosAoFazerPatch() throws Exception {
        // ARRANGE
        PecaResponseDTO dtoAtualizado = new PecaResponseDTO(
                "SKU123", "FILTRO DE ÓLEO", "PN-999",
                new BigDecimal("220.00"), new BigDecimal("130.00"),
                8, 5, "FILTRO DE ÓLEO BOSCH - GOL 1.0"
        );
        when(pecaService.atualizarPeca(eq(1L), any(AtualizarPecaRequestDTO.class)))
                .thenReturn(dtoAtualizado);

        AtualizarPecaRequestDTO dto = new AtualizarPecaRequestDTO(
                new BigDecimal("220.00"), new BigDecimal("130.00"), 8);

        // ACT & ASSERT
        mockMvc.perform(patch("/pecas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precoVenda").value(220.00))
                .andExpect(jsonPath("$.precoCusto").value(130.00));

        verify(pecaService, times(1)).atualizarPeca(eq(1L), any(AtualizarPecaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao fazer PATCH em peça com ID inexistente")
    void deveRetornar404AoAtualizarPecaComIdInexistente() throws Exception {
        // ARRANGE
        when(pecaService.atualizarPeca(eq(999L), any(AtualizarPecaRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Peça não encontrada com ID: 999"));

        AtualizarPecaRequestDTO dto = new AtualizarPecaRequestDTO(
                new BigDecimal("100.00"), new BigDecimal("50.00"), 3);

        // ACT & ASSERT
        mockMvc.perform(patch("/pecas/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Peça não encontrada com ID: 999"));
    }

    // =========================================================================
    // DELETE /pecas/{id}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 204 No Content e acionar o serviço ao excluir peça com sucesso")
    void deveRetornar204AoExcluirPecaComSucesso() throws Exception {
        // ARRANGE
        doNothing().when(pecaService).ExcluirPeca(1L);

        // ACT & ASSERT
        mockMvc.perform(delete("/pecas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(pecaService, times(1)).ExcluirPeca(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao excluir peça com ID inexistente")
    void deveRetornar404AoExcluirPecaComIdInexistente() throws Exception {
        // ARRANGE
        doThrow(new ResourceNotFoundException("Peça não encontrada com ID: 999"))
                .when(pecaService).ExcluirPeca(999L);

        // ACT & ASSERT
        mockMvc.perform(delete("/pecas/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Peça não encontrada com ID: 999"));
    }

    // =========================================================================
    // Fábricas de Dados (Test Fixtures)
    // =========================================================================

    private PecaResponseDTO fabricarPecaResponseDTO() {
        return new PecaResponseDTO(
                "SKU123",
                "FILTRO DE ÓLEO",
                "PN-999",
                new BigDecimal("150.00"),
                new BigDecimal("100.00"),
                10,
                5,
                "FILTRO DE ÓLEO BOSCH - GOL 1.0"
        );
    }

    /**
     * Payload JSON válido alinhado às restrições do PecaRequestDTO:
     * sku @NotBlank, nome @NotBlank, precoVenda @Positive, quantidadeEstoque @Min(0), etc.
     */
    private String jsonPayloadPecaValida() {
        return """
                {
                    "sku": "SKU123",
                    "nome": "Filtro de Óleo",
                    "partNumber": "PN-999",
                    "marca": "Bosch",
                    "aplicacao": "Gol 1.0",
                    "precoVenda": 150.00,
                    "precoCusto": 100.00,
                    "quantidadeEstoque": 10,
                    "estoqueMinimo": 5
                }
                """;
    }
}
