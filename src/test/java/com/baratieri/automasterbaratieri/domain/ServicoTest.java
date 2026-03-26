package com.baratieri.automasterbaratieri.domain;

import com.baratieri.automasterbaratieri.entities.Servico;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ServicoTest {

    private Servico servico;

    @BeforeEach
    void setUp() {
        servico = new Servico();
    }

    // =========================================================================
    // preencherDados — Caminho Feliz
    // =========================================================================

    @Test
    @DisplayName("Deve preencher dados com sucesso quando valores forem válidos")
    void devePreencherDadosComSucessoQuandoValoresForemValidos() {
        // ARRANGE
        String descricao = "Troca de óleo do motor";
        BigDecimal valor = new BigDecimal("150.00");

        // ACT
        servico.preencherDados(descricao, valor);

        // ASSERT
        assertEquals("TROCA DE ÓLEO DO MOTOR", servico.getDescricao());
        assertEquals(new BigDecimal("150.00"), servico.getValorMaoDeObraBase());
    }

    @Test
    @DisplayName("Deve formatar descrição para maiúsculo e sem espaços nas bordas")
    void deveFormatarDescricaoParaMaiusculoESemEspacos() {
        // ARRANGE
        String descricaoSuja = "  alinhamento e balanceamento  ";
        BigDecimal valor = new BigDecimal("200.00");

        // ACT
        servico.preencherDados(descricaoSuja, valor);

        // ASSERT
        assertEquals("ALINHAMENTO E BALANCEAMENTO", servico.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar valor de mão de obra nulo ou zero")
    void deveAceitarValorDeMaoDeObraNuloOuZero() {
        // ACT & ASSERT
        assertDoesNotThrow(() -> servico.preencherDados("Serviço com valor zero", BigDecimal.ZERO));
        assertEquals(BigDecimal.ZERO, servico.getValorMaoDeObraBase());

        assertDoesNotThrow(() -> servico.preencherDados("Serviço com valor nulo", null));
        assertNull(servico.getValorMaoDeObraBase());
    }

    // =========================================================================
    // preencherDados — Cenários de Exceção
    // =========================================================================

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao preencher dados com descrição nula")
    void deveLancarExcecaoAoPreencherDadosComDescricaoNula() {
        // ARRANGE
        BigDecimal valor = new BigDecimal("50.00");

        // ACT & ASSERT
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            servico.preencherDados(null, valor);
        });
        assertEquals("A descrição do serviço é obrigatória.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao preencher dados com descrição em branco")
    void deveLancarExcecaoAoPreencherDadosComDescricaoEmBranco() {
        // ARRANGE
        BigDecimal valor = new BigDecimal("50.00");

        // ACT & ASSERT
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            servico.preencherDados("   ", valor);
        });
        assertEquals("A descrição do serviço é obrigatória.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao preencher dados com valor de mão de obra negativo")
    void deveLancarExcecaoAoPreencherDadosComValorDeMaoDeObraNegativo() {
        // ARRANGE
        BigDecimal valorNegativo = new BigDecimal("-10.00");

        // ACT & ASSERT
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            servico.preencherDados("Serviço Inválido", valorNegativo);
        });
        assertEquals("O valor da mão de obra não pode ser negativo", exception.getMessage());
    }

}
