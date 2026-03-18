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
    private final BigDecimal valorTotalOs = new BigDecimal("200.00");

    @BeforeEach
    void setUp() {

        Veiculo veiculo = new Veiculo();
        ordemServico = new OrdemServico(veiculo, "Troca de óleo");
        ordemServico.setValorTotal(valorTotalOs);
    }

    @Test
    @DisplayName("Deve adicionar pagamento parcial com sucesso")
    void deveAdicionarPagamentoParcialComSucesso() {

        Pagamento pagamento = fabricaPagamentoComCartao(new BigDecimal("100.00"));
        ordemServico.adicionarPagamento(pagamento);

        assertEquals(1, ordemServico.getPagamentos().size());
        assertEquals(new BigDecimal("100.00"), ordemServico.getPagamentos().get(0).getValor());
    }

    @Test
    @DisplayName("Deve zerar o saldo e mudar status para PAGO")
    void deveZerarSaldoEMudarStatusParaPago() {
        Pagamento pagamento = fabricaPagamentoComCartao(valorTotalOs);
        ordemServico.adicionarPagamento(pagamento);

        assertEquals(1, ordemServico.getPagamentos().size());
        assertEquals(StatusOS.PAGO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pagar valor maior que saldo devedor no cartão")
    void deveLancarExcecaoAoTentarPagarValorMaiorQueSaldoDevedorNoCartao() {

        Pagamento pagamento = fabricaPagamentoComCartao(new BigDecimal("300.00"));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            ordemServico.adicionarPagamento(pagamento);
        });

        assertEquals("O valor do pagamento (300.00) não pode ser maior que o saldo devedor atual (200.00).", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar pagamento em O.S. já paga")
    void deveLancarExcecaoAoTentarAdicionarPagamentoEmOSJaPaga() {
        Pagamento pagamentoInicial = fabricaPagamentoComCartao(valorTotalOs);
        ordemServico.adicionarPagamento(pagamentoInicial);

        Pagamento novoPagamento = fabricaPagamentoComCartao(new BigDecimal("50.00"));

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

    @Test
    @DisplayName("Deve calcular o valor total somando os subtotais de peças e serviços.")
    void deveCalcularvalorTotalComSucesso() {
        Peca peca = new Peca();
        Servico servico = new Servico();
        // 2 Pneus de R$ 300,00 cada = Subtotal de R$ 600,00
        ItemPeca itemPeca = new ItemPeca(ordemServico, peca, 2, new BigDecimal("300.00"));
        // 1 Alinhamento 3D = Subtotal de R$ 100,00
        ItemServico itemServico = new ItemServico(ordemServico, servico, null,
                new BigDecimal("100.00"), 1, "Alinhamento 3D");

        ordemServico.adicionarServico(itemServico);
        ordemServico.adicionarPecaOrdemServico(itemPeca);

        // O valor original do setUp (200.00) é substituído pelo novo cálculo.
        // 600 (Peças) + 100 (Serviços) = 700.00
        assertEquals(new BigDecimal("700.00"), ordemServico.getValorTotal());
    }

    @Test
    @DisplayName("Deve recalcular e subtrair do valor total quando uma peça for removida")
    void deveSubtrairValorTotalAoSubtrairPeca() {

        Peca peca = new Peca();
        peca.setQuantidadeEstoque(10);

        ItemPeca filtroAr = new ItemPeca(ordemServico, peca, 1, new BigDecimal("50.00"));
        filtroAr.setId(1L);

        ItemPeca oleo = new ItemPeca(ordemServico, peca, 1, new BigDecimal("150.00"));
        oleo.setId(2L);

        ordemServico.adicionarPecaOrdemServico(filtroAr);
        ordemServico.adicionarPecaOrdemServico(oleo);

        ordemServico.removerPecaOrdemServico(oleo);

        assertEquals(new BigDecimal("50.00"), ordemServico.getValorTotal());
    }

    @Test
    @DisplayName("Deve aprovar orçamento e mudar status para EM_EXECUCAO")
    void deveAprovarOrcamentoComSucesso() {
        ordemServico.aprovarOrcamento();

        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar aprovar uma O.S. cancelada")
    void deveLancarExcecaoAoAprovarOsCancelada() {
        ordemServico.cancelarOs();
        assertEquals(StatusOS.CANCELADO, ordemServico.getStatus());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            ordemServico.aprovarOrcamento();
        });

        assertEquals("Não é possível aprovar um orçamento de uma" +
                " Ordem de Serviço que foi cancelada.", exception.getMessage());
    }


    @Test
    @DisplayName("Deve lançar exceção ao validar aprovação de O.S. sem peças e sem serviços")
    void deveLancarExcecaoAoValidarAprovacaoSemItens() {

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            ordemServico.validarPecasOrdemServicoAprovada();
        });

        assertEquals("Não é possível aprovar a Ordem de Serviço. Adicione pelo menos uma Peça ou Serviço ao orçamento.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cancelar a O.S. com sucesso e registrar a data de fechamento")
    void deveCancelarOsComSucesso() {
        ordemServico.cancelarOs();

        assertEquals(StatusOS.CANCELADO, ordemServico.getStatus());
        assertNotNull(ordemServico.getDataFechamento());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar uma O.S. finalizada")
    void deveLancarExcecaoAoCancelarOsFinalizada() {
        ordemServico.aprovarOrcamento();
        ordemServico.finalizarOs();

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            ordemServico.cancelarOs();
        });

        assertEquals("Não é possível cancelar uma Ordem de Serviço que já foi finalizada e entregue ao cliente.", exception.getMessage());
    }

    private Pagamento fabricaPagamentoComCartao(BigDecimal valor) {
        return new PagamentoCartao(ordemServico, valor, 1, "Visa");
    }

}
