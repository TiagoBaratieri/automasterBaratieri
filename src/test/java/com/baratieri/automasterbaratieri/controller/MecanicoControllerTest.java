package com.baratieri.automasterbaratieri.controller;


import com.baratieri.automasterbaratieri.dto.request.AtualizarMecanicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.request.MecanicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.MecanicoResponseDTO;
import com.baratieri.automasterbaratieri.services.MecanicoService;
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

@WebMvcTest(MecanicoController.class)
class MecanicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MecanicoService mecanicoService;

    // =========================================================================
    // GET /mecanicos/{id}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com DTO correto ao buscar mecânico por ID existente")
    void deveRetornar200ComDtoCorretoAoBuscarMecanicoPorId() throws Exception {
        // ARRANGE
        when(mecanicoService.buscarMecanicoPorId(1L))
                .thenReturn(fabricarMecanicoResponseDTO());

        // ACT & ASSERT
        mockMvc.perform(get("/mecanicos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("CARLOS SILVA"))
                .andExpect(jsonPath("$.especialidade").value("MOTOR"))
                .andExpect(jsonPath("$.taxaComissao").value(10.00));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando mecânico não for encontrado pelo ID")
    void deveRetornar404QuandoMecanicoNaoForEncontradoPeloId() throws Exception {
        // ARRANGE
        when(mecanicoService.buscarMecanicoPorId(999L))
                .thenThrow(new ResourceNotFoundException("Mecânico não encontrada com ID: 999"));

        // ACT & ASSERT
        mockMvc.perform(get("/mecanicos/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Mecânico não encontrada com ID: 999"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o ID for um texto não-numérico")
    void deveRetornar400QuandoIdForTextoInvalido() throws Exception {
        // ACT & ASSERT — "abc" não pode ser convertido para Long
        // MethodArgumentTypeMismatchException → ResourceExceptionHandler → 400
        mockMvc.perform(get("/mecanicos/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Parâmetro de URL inválido"));
    }

    // =========================================================================
    // GET /mecanicos
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com Page de mecânicos ao buscar com filtros")
    void deveRetornar200ComPaginaDeMecanicosAoBuscarComFiltros() throws Exception {
        // ARRANGE
        PageImpl<MecanicoResponseDTO> paginaFake = new PageImpl<>(
                List.of(fabricarMecanicoResponseDTO()),
                PageRequest.of(0, 10),
                1
        );
        when(mecanicoService.buscarMecanico(any(), any(), any(), any()))
                .thenReturn(paginaFake);

        // ACT & ASSERT
        mockMvc.perform(get("/mecanicos")
                        .param("nome", "carlos")
                        .param("especialidade", "motor")
                        .param("ativo", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("CARLOS SILVA"))
                .andExpect(jsonPath("$.content[0].especialidade").value("MOTOR"))
                .andExpect(jsonPath("$.content[0].ativo").value(true))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // =========================================================================
    // POST /mecanicos
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 201 Created com header Location ao salvar mecânico com payload válido")
    void deveRetornar201ComLocationAoSalvarMecanicoValido() throws Exception {
        // ARRANGE
        when(mecanicoService.salvarMecanico(any(MecanicoRequestDTO.class)))
                .thenReturn(fabricarMecanicoResponseDTO());

        // ACT & ASSERT
        mockMvc.perform(post("/mecanicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayloadMecanicoValido()))
                .andExpect(status().isCreated())
                // .path("/{id}").build() sem buildAndExpand → Location não contém ID real
                // só é possível verificar a existência do header
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("CARLOS SILVA"))
                .andExpect(jsonPath("$.taxaComissao").value(10.00));

        verify(mecanicoService, times(1))
                .salvarMecanico(any(MecanicoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 422 Unprocessable Entity quando payload do POST estiver vazio")
    void deveRetornar422QuandoPayloadDoPostForInvalido() throws Exception {
        // ARRANGE — "{}" dispara @NotBlank (nome, cpf, especialidade) e @NotNull (taxaComissao, ativo)
        // todos definidos em MecanicoRequestDTO

        // ACT & ASSERT
        mockMvc.perform(post("/mecanicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Erro de Validação"));

        // Garante que o serviço não é acionado quando a validação falha
        verify(mecanicoService, never()).salvarMecanico(any());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o serviço rejeitar CPF duplicado")
    void deveRetornar400QuandoCpfJaEstiverCadastrado() throws Exception {
        // ARRANGE
        when(mecanicoService.salvarMecanico(any(MecanicoRequestDTO.class)))
                .thenThrow(new RegraNegocioException(
                        "Já existe um(a) Mecânico cadastrado(a) com este documento: 123.456.789-09"));

        // ACT & ASSERT
        mockMvc.perform(post("/mecanicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayloadMecanicoValido()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Já existe um(a) Mecânico cadastrado(a) com este documento: 123.456.789-09"));
    }

    // =========================================================================
    // PATCH /mecanicos/{id}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com taxa de comissão atualizada ao fazer PATCH")
    void deveRetornar200ComTaxaAtualizadaAoFazerPatch() throws Exception {
        // ARRANGE
        MecanicoResponseDTO dtoAtualizado = new MecanicoResponseDTO(
                1L, "CARLOS SILVA", "12345678909", "MOTOR",
                new BigDecimal("25.00"), true
        );
        when(mecanicoService.atualizarMecanico(eq(1L), any(AtualizarMecanicoRequestDTO.class)))
                .thenReturn(dtoAtualizado);

        AtualizarMecanicoRequestDTO dto =
                new AtualizarMecanicoRequestDTO(new BigDecimal("25.00"), true);

        // ACT & ASSERT
        mockMvc.perform(patch("/mecanicos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taxaComissao").value(25.00))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(mecanicoService, times(1))
                .atualizarMecanico(eq(1L), any(AtualizarMecanicoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao fazer PATCH em mecânico com ID inexistente")
    void deveRetornar404AoAtualizarMecanicoComIdInexistente() throws Exception {
        // ARRANGE
        when(mecanicoService.atualizarMecanico(eq(999L), any(AtualizarMecanicoRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Mecânico não encontrada com ID: 999"));

        AtualizarMecanicoRequestDTO dto =
                new AtualizarMecanicoRequestDTO(new BigDecimal("10.00"), true);

        // ACT & ASSERT
        mockMvc.perform(patch("/mecanicos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Mecânico não encontrada com ID: 999"));
    }

    // =========================================================================
    // DELETE /mecanicos/{id}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK ao excluir mecânico com sucesso")
    void deveRetornar200AoExcluirMecanicoComSucesso() throws Exception {
        // ARRANGE
        doNothing().when(mecanicoService).excluirMecanico(1L);

        // ACT & ASSERT
        // ATENÇÃO: MecanicoController usa ResponseEntity.ok().build() no DELETE
        // → retorna 200 OK, diferente de ClienteController e PecaController que retornam 204
        mockMvc.perform(delete("/mecanicos/{id}", 1L))
                .andExpect(status().isOk());

        verify(mecanicoService, times(1)).excluirMecanico(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao excluir mecânico com ID inexistente")
    void deveRetornar404AoExcluirMecanicoComIdInexistente() throws Exception {
        // ARRANGE
        doThrow(new ResourceNotFoundException("Mecânico não encontrada com ID: 999"))
                .when(mecanicoService).excluirMecanico(999L);

        // ACT & ASSERT
        mockMvc.perform(delete("/mecanicos/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Mecânico não encontrada com ID: 999"));
    }

    // =========================================================================
    // Fábricas de Dados (Test Fixtures)
    // =========================================================================

    private MecanicoResponseDTO fabricarMecanicoResponseDTO() {
        return new MecanicoResponseDTO(
                1L,
                "CARLOS SILVA",
                "12345678909",
                "MOTOR",
                new BigDecimal("10.00"),
                true
        );
    }

    /**
     * Payload JSON válido alinhado às restrições do MecanicoRequestDTO:
     * - nome: @NotBlank + @Size(min=3, max=100)
     * - cpf: @NotBlank + @CPF (formato válido)
     * - especialidade: @NotBlank
     * - taxaComissao: @NotNull + @DecimalMin("0.00") + @DecimalMax("100.00")
     * - ativo: @NotNull
     */
    private String jsonPayloadMecanicoValido() {
        return """
                {
                    "nome": "Carlos Silva",
                    "cpf": "123.456.789-09",
                    "especialidade": "Motor",
                    "taxaComissao": 10.00,
                    "ativo": true
                }
                """;
    }
}
