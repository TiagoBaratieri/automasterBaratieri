package com.baratieri.automasterbaratieri.controller;


import com.baratieri.automasterbaratieri.dto.request.ClienteAtualizacaoRequestDTO;
import com.baratieri.automasterbaratieri.dto.request.ClienteRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ClienteResponseDTO;
import com.baratieri.automasterbaratieri.services.ClienteService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    // =========================================================================
    // GET /clientes/{id}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com o DTO correto ao buscar cliente por ID existente")
    void deveRetornar200ComDtoCorretoAoBuscarClientePorId() throws Exception {
        // ARRANGE
        when(clienteService.buscarClientePorId(1L)).thenReturn(fabricarClienteResponseDTO());

        // ACT & ASSERT
        mockMvc.perform(get("/clientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("JOÃO DA SILVA"))
                .andExpect(jsonPath("$.cpfOuCnpj").value("12345678909"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando cliente não for encontrado pelo ID")
    void deveRetornar404QuandoClienteNaoForEncontradoPeloId() throws Exception {
        // ARRANGE
        when(clienteService.buscarClientePorId(999L))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrada com ID: 999"));

        // ACT & ASSERT
        mockMvc.perform(get("/clientes/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente não encontrada com ID: 999"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o ID for um texto não-numérico")
    void deveRetornar400QuandoIdForTextoInvalido() throws Exception {
        // ACT & ASSERT — "abc" não pode ser convertido para Long
        // MethodArgumentTypeMismatchException → ResourceExceptionHandler → 400
        mockMvc.perform(get("/clientes/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Parâmetro de URL inválido"));
    }

    // =========================================================================
    // GET /clientes
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com Page de clientes ao listar com filtros")
    void deveRetornar200ComPaginaDeClientesAoListarComFiltros() throws Exception {
        // ARRANGE
        PageImpl<ClienteResponseDTO> paginaFake = new PageImpl<>(
                List.of(fabricarClienteResponseDTO()),
                PageRequest.of(0, 10),
                1
        );
        when(clienteService.listarClientes(any(), any(), any())).thenReturn(paginaFake);

        // ACT & ASSERT
        mockMvc.perform(get("/clientes")
                        .param("nome", "joão")
                        .param("cpfOuCnpj", "123.456.789-09"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("JOÃO DA SILVA"))
                .andExpect(jsonPath("$.content[0].cpfOuCnpj").value("12345678909"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // =========================================================================
    // POST /clientes
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 201 Created com header Location contendo o ID do novo cliente")
    void deveRetornar201ComLocationCorretaAoSalvarClienteValido() throws Exception {
        // ARRANGE
        when(clienteService.salvarCliente(any(ClienteRequestDTO.class)))
                .thenReturn(fabricarClienteResponseDTO());

        // ACT & ASSERT
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayloadClienteValido()))
                .andExpect(status().isCreated())
                // buildAndExpand(clienteDto.id()) → URL contém o ID real do recurso criado
                .andExpect(header().string("Location", "http://localhost/clientes/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("JOÃO DA SILVA"));

        verify(clienteService, times(1)).salvarCliente(any(ClienteRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o serviço rejeitar CPF/CNPJ duplicado")
    void deveRetornar400QuandoCpfJaEstiverCadastrado() throws Exception {
        // ARRANGE
        // Nota: ClienteRequestDTO NÃO tem @NotBlank/@NotNull — não há 422 neste endpoint.
        // A rejeição ocorre no domínio (RegraNegocioException) → 400 Bad Request.
        when(clienteService.salvarCliente(any(ClienteRequestDTO.class)))
                .thenThrow(new RegraNegocioException(
                        "Já existe um(a) Cliente cadastrado(a) com este documento: 123.456.789-09"));

        // ACT & ASSERT
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayloadClienteValido()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Já existe um(a) Cliente cadastrado(a) com este documento: 123.456.789-09"));
    }

    // =========================================================================
    // PATCH /clientes/{id}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 200 OK com dados de contato atualizados ao fazer PATCH")
    void deveRetornar200ComDadosAtualizadosAoFazerPatch() throws Exception {
        // ARRANGE
        ClienteResponseDTO dtoAtualizado = new ClienteResponseDTO(
                1L, "JOÃO DA SILVA", "12345678909",
                "(41) 88888-8888", "novo@email.com", null
        );
        when(clienteService.atualizarCliente(eq(1L), any(ClienteAtualizacaoRequestDTO.class)))
                .thenReturn(dtoAtualizado);

        ClienteAtualizacaoRequestDTO dto =
                new ClienteAtualizacaoRequestDTO(null, "(41) 88888-8888", "novo@email.com");

        // ACT & ASSERT
        mockMvc.perform(patch("/clientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("novo@email.com"))
                .andExpect(jsonPath("$.telefone").value("(41) 88888-8888"));

        verify(clienteService, times(1))
                .atualizarCliente(eq(1L), any(ClienteAtualizacaoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 422 Unprocessable Entity quando o e-mail do PATCH for inválido")
    void deveRetornar422QuandoEmailDoPatchForInvalido() throws Exception {
        // ARRANGE — @Email em ClienteAtualizacaoRequestDTO dispara Bean Validation
        String payloadEmailInvalido = """
                {
                    "telefone": "(41) 99999-9999",
                    "email": "email-invalido-sem-arroba"
                }
                """;

        // ACT & ASSERT
        mockMvc.perform(patch("/clientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadEmailInvalido))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Erro de Validação"));

        // Garante que o serviço não é chamado quando a validação do payload falha
        verify(clienteService, never()).atualizarCliente(any(), any());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao fazer PATCH em cliente com ID inexistente")
    void deveRetornar404AoAtualizarClienteComIdInexistente() throws Exception {
        // ARRANGE
        when(clienteService.atualizarCliente(eq(999L), any(ClienteAtualizacaoRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrada com ID: 999"));

        ClienteAtualizacaoRequestDTO dto =
                new ClienteAtualizacaoRequestDTO(null, null, "email@email.com");

        // ACT & ASSERT
        mockMvc.perform(patch("/clientes/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente não encontrada com ID: 999"));
    }

    // =========================================================================
    // DELETE /clientes/{id}
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 204 No Content e acionar o serviço ao excluir cliente com sucesso")
    void deveRetornar204AoExcluirClienteComSucesso() throws Exception {
        // ARRANGE
        doNothing().when(clienteService).excluirCliente(1L);

        // ACT & ASSERT
        mockMvc.perform(delete("/clientes/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(clienteService, times(1)).excluirCliente(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao excluir cliente com ID inexistente")
    void deveRetornar404AoExcluirClienteComIdInexistente() throws Exception {
        // ARRANGE
        doThrow(new ResourceNotFoundException("Cliente não encontrada com ID: 999"))
                .when(clienteService).excluirCliente(999L);

        // ACT & ASSERT
        mockMvc.perform(delete("/clientes/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente não encontrada com ID: 999"));
    }

    // =========================================================================
    // Fábricas de Dados (Test Fixtures)
    // =========================================================================

    private ClienteResponseDTO fabricarClienteResponseDTO() {
        return new ClienteResponseDTO(
                1L,
                "JOÃO DA SILVA",
                "12345678909",
                "(41) 99999-9999",
                "joao@email.com",
                null // endereço omitido para simplificar — não é validado nos cenários acima
        );
    }

    /**
     * Payload JSON válido para POST. ClienteRequestDTO não tem anotações
     * de Bean Validation, portanto qualquer JSON bem formado passa pela
     * validação HTTP — a rejeição acontece apenas no domínio (serviço/entidade).
     */
    private String jsonPayloadClienteValido() {
        return """
                {
                    "nome": "João da Silva",
                    "cpfOuCnpj": "123.456.789-09",
                    "telefone": "(41) 99999-9999",
                    "email": "joao@email.com"
                }
                """;
    }
}
