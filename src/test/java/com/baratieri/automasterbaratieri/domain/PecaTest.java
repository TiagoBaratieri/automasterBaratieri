package com.baratieri.automasterbaratieri.domain;

import com.baratieri.automasterbaratieri.entities.Peca;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PecaTest {

    private Peca peca;

    @BeforeEach
    void setUp() {
       peca = fabricarPecaPadrao();
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar Peça sem nome")
    void deveLancarExcecaoAoCriarPecaSemNome() {

        String nomePeca = null;
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            new Peca("SKU123", null, "PN-999", "Bosch", "Gol 1.0",
                    new BigDecimal("50.00"), new BigDecimal("20.00"), 10, 5);
        });
        assertEquals("O nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve baixar o estoque com sucesso")
    void deveBaixarEstoqueComSucesso() {

        Integer quantidadeParaBaixar = 3;

        peca.baixarEstoque(quantidadeParaBaixar);

        assertEquals(7, peca.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar baixar mais estoque do que o existente")
    void deveLancarExcecaoAoBaixarEstoqueMaiorQueOExistente() {

        Integer quantidadeParaBaixar = 15;

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            peca.baixarEstoque(quantidadeParaBaixar);
        });

        assertEquals("Estoque insuficiente! Você tentou baixar 15" +
                " mas só existem 10 peças disponíveis.", exception.getMessage());

    }

    @Test
    @DisplayName("Deve retornar true Quando o estoque atingir o minimo.")
    void deveRetornarTrueQuandoEstoqueAtingirMinimo() {

        Integer quantidadeParaBaixar = 5;

        peca.baixarEstoque(quantidadeParaBaixar);

        assertTrue(peca.precisaReporEstoque());
    }

    private Peca fabricarPecaPadrao() {
        return new Peca("SKU123",
                "Filtro de Óleo",
                "PN-999",
                "Bosch",
                "Gol 1.0",
                new BigDecimal("50.00"),
                new BigDecimal("20.00"),
                10,
                5);
    }
}
