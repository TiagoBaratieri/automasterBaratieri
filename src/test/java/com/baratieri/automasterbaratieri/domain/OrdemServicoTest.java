package com.baratieri.automasterbaratieri.domain;

import com.baratieri.automasterbaratieri.entities.*;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrdemServicoTest {

    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        Veiculo veiculo = new Veiculo();
        ordemServico = new OrdemServico(veiculo, "Troca de óleo");
        ordemServico.setValorTotal(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("Deve adicionar pagamento parcial com sucesso")
    void deveAdicionarPagamentoParcialComSucesso() {
        Pagamento pagamento = new PagamentoCartao(ordemServico, new BigDecimal("100.00"), 1, "Visa");
        ordemServico.adicionarPagamento(pagamento);

        assertEquals(1, ordemServico.getPagamentos().size());
        assertEquals(new BigDecimal("100.00"), ordemServico.getPagamentos().get(0).getValor());
    }

    @Test
    @DisplayName("Deve zerar o saldo e mudar status para PAGO")
    void deveZerarSaldoEMudarStatusParaPago() {
        Pagamento pagamento = new PagamentoCartao(ordemServico, new BigDecimal("200.00"), 1, "Visa");
        ordemServico.adicionarPagamento(pagamento);

        assertEquals(1, ordemServico.getPagamentos().size());
        assertEquals(StatusOS.PAGO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pagar valor maior que saldo devedor no cartão")
    void deveLancarExcecaoAoTentarPagarValorMaiorQueSaldoDevedorNoCartao() {
        Pagamento pagamento = new PagamentoCartao(ordemServico, new BigDecimal("300.00"), 1, "Visa");

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            ordemServico.adicionarPagamento(pagamento);
        });

        assertEquals("O valor do pagamento (300.00) não pode ser maior que o saldo devedor atual (200.00).", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar pagamento em O.S. já paga")
    void deveLancarExcecaoAoTentarAdicionarPagamentoEmOSJaPaga() {
        Pagamento pagamentoInicial = new PagamentoCartao(ordemServico, new BigDecimal("200.00"), 1, "Visa");
        ordemServico.adicionarPagamento(pagamentoInicial);

        Pagamento novoPagamento = new PagamentoCartao(ordemServico, new BigDecimal("50.00"), 1, "Mastercard");

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            ordemServico.adicionarPagamento(novoPagamento);
        });

        assertEquals("Esta Ordem de Serviço já se encontra totalmente paga.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve permitir pagamento maior em Dinheiro")
    void devePermitirPagamentoMaiorEmDinheiro() {
        Pagamento pagamentoInicial = new PagamentoDinheiro(ordemServico, new BigDecimal("100.00"), new BigDecimal("150.00"));
        ordemServico.adicionarPagamento(pagamentoInicial);

        assertEquals(1, ordemServico.getPagamentos().size());
        assertEquals(new BigDecimal("100.00"), ordemServico.getPagamentos().get(0).getValor());
    }
}
