package com.baratieri.automasterbaratieri.domain;

import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.entities.Pagamento;
import com.baratieri.automasterbaratieri.enums.StatusPagamento;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PagamentoTest {

    @Mock
    private OrdemServico ordemServicoMock;
    private Pagamento pagamento;

    // Classe aninhada estática para instanciar a classe abstrata Pagamento
    private static class PagamentoConcreto extends Pagamento {
        public PagamentoConcreto(BigDecimal valor, OrdemServico ordemServico) {
            super(valor, ordemServico);
        }
    }

    @BeforeEach
    void setUp() {
        pagamento = fabricarPagamentoPadrao();
    }

    // =========================================================================
    // Construtor — Estado Inicial
    // =========================================================================

    @Test
    @DisplayName("Deve criar pagamento com status PENDENTE por padrão")
    void deveCriarPagamentoComStatusPendentePorPadrao() {
        // ASSERT
        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatusPagamento());
        assertNull(pagamento.getDataPagamento(), "Data do pagamento deve ser nula na criação");
    }

    @Test
    @DisplayName("Deve criar pagamento com valor e ordem de serviço corretos")
    void deveCriarPagamentoComValorEOrdemCorretos() {
        // ASSERT
        assertEquals(new BigDecimal("150.75"), pagamento.getValor());
        assertEquals(ordemServicoMock, pagamento.getOrdemServico());
    }

    // =========================================================================
    // Construtor — Validações de Domínio
    // =========================================================================

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar pagamento com valor nulo")
    void deveLancarExcecaoAoCriarPagamentoComValorNulo() {
        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new PagamentoConcreto(null, ordemServicoMock));

        assertEquals("O valor do pagamento deve ser maior que zero.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar pagamento com valor zero")
    void deveLancarExcecaoAoCriarPagamentoComValorZero() {
        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new PagamentoConcreto(BigDecimal.ZERO, ordemServicoMock));

        assertEquals("O valor do pagamento deve ser maior que zero.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar pagamento com valor negativo")
    void deveLancarExcecaoAoCriarPagamentoComValorNegativo() {
        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new PagamentoConcreto(new BigDecimal("-50.00"), ordemServicoMock));

        assertEquals("O valor do pagamento deve ser maior que zero.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar pagamento com ordem de serviço nula")
    void deveLancarExcecaoAoCriarPagamentoComOrdemDeServicoNula() {
        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new PagamentoConcreto(new BigDecimal("100.00"), null));

        assertEquals("A ordem de serviço é obrigatória para o pagamento.", excecao.getMessage());
    }

    // =========================================================================
    // confirmarPagamento
    // =========================================================================

    @Test
    @DisplayName("Deve confirmar pagamento com sucesso quando status for PENDENTE")
    void deveConfirmarPagamentoComSucesso() {
        // ARRANGE
        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatusPagamento());

        // ACT
        pagamento.confirmarPagamento();

        // ASSERT
        assertEquals(StatusPagamento.PAGO, pagamento.getStatusPagamento());
        assertNotNull(pagamento.getDataPagamento());
        assertTrue(pagamento.getDataPagamento().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar confirmar um pagamento já PAGO")
    void deveLancarExcecaoAoConfirmarPagamentoJaPago() {
        // ARRANGE
        pagamento.confirmarPagamento(); // Primeiro pagamento
        assertEquals(StatusPagamento.PAGO, pagamento.getStatusPagamento());

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                pagamento.confirmarPagamento()); // Tenta pagar de novo

        assertEquals("Este pagamento já foi processado anteriormente.", excecao.getMessage());
    }

    // =========================================================================
    // validarStatusPagamento
    // =========================================================================

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao validar status de pagamento quando for nulo")
    void deveLancarExcecaoAoValidarStatusNulo() {
        // ARRANGE
        pagamento.setStatusPagamento(null);

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                pagamento.validarStatusPagamento());

        assertEquals("O status do pagamento não pode ser nulo.", excecao.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção ao validar status de pagamento quando não for nulo")
    void naoDeveLancarExcecaoAoValidarStatusNaoNulo() {
        // ARRANGE
        pagamento.setStatusPagamento(StatusPagamento.PENDENTE);
        // ACT & ASSERT
        assertDoesNotThrow(() -> pagamento.validarStatusPagamento());

        // ARRANGE
        pagamento.setStatusPagamento(StatusPagamento.PAGO);
        // ACT & ASSERT
        assertDoesNotThrow(() -> pagamento.validarStatusPagamento());
    }

    // =========================================================================
    // Fábrica de dados
    // =========================================================================

    private Pagamento fabricarPagamentoPadrao() {
        return new PagamentoConcreto(new BigDecimal("150.75"), ordemServicoMock);
    }
}
