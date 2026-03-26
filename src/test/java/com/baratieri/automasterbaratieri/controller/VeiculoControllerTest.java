package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.VeiculoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.VeiculoResponseDTO;
import com.baratieri.automasterbaratieri.services.VeiculoService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VeiculoController.class)
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VeiculoService veiculoService;

    // =========================================================================
    // GET /veiculos/{id}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com DTO correto ao buscar veículo por ID existente")
    void deveRetornar200ComDtoCorretoAoBuscarVeiculoPorId() throws Exception {
        // ARRANGE
        when(veiculoService.buscarVeiculoPorId(1L)).thenReturn(fabricarVeiculoResponseDTO());

        // ACT & ASSERT
        mockMvc.perform(get("/veiculos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.placa").value("BRA2E19"))
                .andExpect(jsonPath("$.modelo").value("MODELO Y"))
                .andExpect(jsonPath("$.marca").value("TESLA"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando veículo não for encontrado pelo ID")
    void deveRetornar404QuandoVeiculoNaoForEncontradoPeloId() throws Exception {
        // ARRANGE
        when(veiculoService.buscarVeiculoPorId(99L)).thenThrow(new ResourceNotFoundException("Veículo não encontrado com ID: 99"));

        // ACT & ASSERT
        mockMvc.perform(get("/veiculos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Veículo não encontrado com ID: 99"));
    }

    // =========================================================================
    // GET /veiculos/placa/{placa}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com DTO correto ao buscar veículo por placa existente")
    void deveRetornar200ComDtoCorretoAoBuscarVeiculoPorPlaca() throws Exception {
        // ARRANGE
        when(veiculoService.buscarVeiculo("BRA2E19")).thenReturn(fabricarVeiculoResponseDTO());

        // ACT & ASSERT
        mockMvc.perform(get("/veiculos/placa/{placa}", "BRA2E19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("BRA2E19"));
    }

    // =========================================================================
    // GET /veiculos
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com Page de veículos ao buscar com filtros")
    void deveRetornar200ComPaginaDeVeiculosAoBuscarComFiltros() throws Exception {
        // ARRANGE
        PageImpl<VeiculoResponseDTO> paginaFake = new PageImpl<>(
                List.of(fabricarVeiculoResponseDTO()),
                PageRequest.of(0, 10),
                1
        );
        when(veiculoService.listarVeiculos(any(), any(), any())).thenReturn(paginaFake);

        // ACT & ASSERT
        mockMvc.perform(get("/veiculos").param("placa", "BRA2E19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].placa").value("BRA2E19"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // =========================================================================
    // POST /veiculos
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 201 Created com header Location ao salvar veículo com payload válido")
    void deveRetornar201ComLocationAoSalvarVeiculoValido() throws Exception {
        // ARRANGE
        when(veiculoService.salvarVeiculo(any(VeiculoRequestDTO.class))).thenReturn(fabricarVeiculoResponseDTO());

        // ACT & ASSERT
        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fabricarVeiculoRequestDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.placa").value("BRA2E19"));

        verify(veiculoService).salvarVeiculo(any(VeiculoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o serviço rejeitar placa duplicada")
    void deveRetornar400QuandoPlacaJaEstiverCadastrada() throws Exception {
        // ARRANGE
        when(veiculoService.salvarVeiculo(any(VeiculoRequestDTO.class)))
                .thenThrow(new RegraNegocioException("Já existe um veículo com a placa informada."));

        // ACT & ASSERT
        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fabricarVeiculoRequestDTO())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Já existe um veículo com a placa informada."));
    }

    // =========================================================================
    // DELETE /veiculos/{id}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 204 No Content ao excluir veículo com sucesso")
    void deveRetornar204AoExcluirVeiculoComSucesso() throws Exception {
        // ARRANGE
        doNothing().when(veiculoService).excluirVeiculo(1L);

        // ACT & ASSERT
        mockMvc.perform(delete("/veiculos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(veiculoService).excluirVeiculo(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao excluir veículo com ID inexistente")
    void deveRetornar404AoExcluirVeiculoComIdInexistente() throws Exception {
        // ARRANGE
        doThrow(new ResourceNotFoundException("Veículo não encontrado com ID: 99"))
                .when(veiculoService).excluirVeiculo(99L);

        // ACT & ASSERT
        mockMvc.perform(delete("/veiculos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Veículo não encontrado com ID: 99"));
    }

    // =========================================================================
    // Fábricas de Dados (Test Fixtures)
    // =========================================================================

    private VeiculoRequestDTO fabricarVeiculoRequestDTO() {
        return new VeiculoRequestDTO("BRA2E19", "Modelo Y", "Tesla", 2023, 1L);
    }

    private VeiculoResponseDTO fabricarVeiculoResponseDTO() {
        return new VeiculoResponseDTO(1L, "BRA2E19", "MODELO Y", "TESLA", 2023);
    }
}
