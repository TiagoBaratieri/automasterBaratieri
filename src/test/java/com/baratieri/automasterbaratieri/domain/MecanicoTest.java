package com.baratieri.automasterbaratieri.domain;

import com.baratieri.automasterbaratieri.entities.Mecanico;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MecanicoTest {

    private Mecanico mecanico;

    @BeforeEach
    void setUp() {
        mecanico = fabricarMecanicoPadrao();
    }

    // =========================================================================
    // Construtor — formatação aplicada pela entidade
    // =========================================================================

    @Test
    @DisplayName("Deve criar mecânico com nome formatado em maiúsculo")
    void deveCriarMecanicoComNomeEmMaiusculo() {
        // O construtor faz formatarTextoOpcional → trim().toUpperCase()
        assertEquals("CARLOS SILVA", mecanico.getNome());
    }

    @Test
    @DisplayName("Deve criar mecânico com especialidade formatada em maiúsculo")
    void deveCriarMecanicoComEspecialidadeEmMaiusculo() {
        assertEquals("MOTOR", mecanico.getEspecialidade());
    }

    @Test
    @DisplayName("Deve formatar nome com espaços e minúsculas para maiúsculo sem espaços extras")
    void deveFormatarNomeComEspacosEMinusculasAoCriarMecanico() {
        // ARRANGE & ACT
        Mecanico mecanicoComNomeSujo = new Mecanico(
                "  carlos silva  ", // espaços nas bordas e letras minúsculas
                "12345678909",
                "Elétrica",
                new BigDecimal("10.00")
        );

        // ASSERT — formatarTextoOpcional → trim().toUpperCase()
        assertEquals("CARLOS SILVA", mecanicoComNomeSujo.getNome());
    }

    @Test
    @DisplayName("Deve iniciar com ativo = true ao criar mecânico")
    void deveCriarMecanicoComoAtivoporPadrao() {
        assertTrue(mecanico.getAtivo(), "Mecânico recém-criado deve ter ativo = true");
    }

    @Test
    @DisplayName("Deve criar mecânico com taxa de comissão correta")
    void deveCriarMecanicoComTaxaDeComissaoCorreta() {
        assertEquals(new BigDecimal("10.00"), mecanico.getTaxaComissao());
    }

    // =========================================================================
    // Construtor — validações de domínio
    // =========================================================================

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar mecânico com nome nulo")
    void deveLancarExcecaoAoCriarMecanicoComNomeNulo() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Mecanico(null, "12345678909", "Motor", new BigDecimal("10.00")));

        assertEquals("O nome do mecânico é obrigatório.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar mecânico com nome em branco")
    void deveLancarExcecaoAoCriarMecanicoComNomeEmBranco() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Mecanico("   ", "12345678909", "Motor", new BigDecimal("10.00")));

        assertEquals("O nome do mecânico é obrigatório.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar mecânico com CPF nulo")
    void deveLancarExcecaoAoCriarMecanicoComCpfNulo() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Mecanico("Carlos Silva", null, "Motor", new BigDecimal("10.00")));

        assertEquals("O CPF do mecânico é obrigatório.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar mecânico com especialidade nula")
    void deveLancarExcecaoAoCriarMecanicoComEspecialidadeNula() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Mecanico("Carlos Silva", "12345678909", null, new BigDecimal("10.00")));

        assertEquals("A especialidade do mecânico é obrigatória.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar mecânico com taxa de comissão nula")
    void deveLancarExcecaoAoCriarMecanicoComTaxaDeComissaoNula() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Mecanico("Carlos Silva", "12345678909", "Motor", null));

        assertEquals("A taxa de comissão não pode ser nula. Ou negativa.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar mecânico com taxa de comissão zero")
    void deveLancarExcecaoAoCriarMecanicoComTaxaDeComissaoZero() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Mecanico("Carlos Silva", "12345678909", "Motor", BigDecimal.ZERO));

        assertEquals("A taxa de comissão não pode ser nula. Ou negativa.", excecao.getMessage());
    }

    // =========================================================================
    // atualizarDados
    // =========================================================================

    @Test
    @DisplayName("Deve atualizar taxa de comissão com sucesso quando valor for válido")
    void deveAtualizarTaxaDeComissaoComSucesso() {
        // ARRANGE
        BigDecimal novaTaxa = new BigDecimal("25.50");

        // ACT
        mecanico.atualizarDados(novaTaxa);

        // ASSERT
        assertEquals(new BigDecimal("25.50"), mecanico.getTaxaComissao());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao atualizar taxa de comissão para zero")
    void deveLancarExcecaoAoAtualizarTaxaDeComissaoParaZero() {
        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                mecanico.atualizarDados(BigDecimal.ZERO));

        assertEquals("A taxa de comissão não pode ser nula. Ou negativa.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao atualizar taxa de comissão para nula")
    void deveLancarExcecaoAoAtualizarTaxaDeComissaoParaNula() {
        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                mecanico.atualizarDados(null));

        assertEquals("A taxa de comissão não pode ser nula. Ou negativa.", excecao.getMessage());
    }

    // =========================================================================
    // inativar / ativar — herdado de Inativavel
    // =========================================================================

    @Test
    @DisplayName("Deve inativar mecânico ao chamar inativar()")
    void deveInativarMecanicoAoInativar() {
        // ACT
        mecanico.inativar();

        // ASSERT
        assertFalse(mecanico.getAtivo());
    }

    @Test
    @DisplayName("Deve reativar mecânico ao chamar ativar() após inativar()")
    void deveReativarMecanicoAposInativar() {
        // ARRANGE
        mecanico.inativar();
        assertFalse(mecanico.getAtivo(), "Pré-condição: mecânico deve estar inativo");

        // ACT
        mecanico.ativar();

        // ASSERT
        assertTrue(mecanico.getAtivo());
    }

    // =========================================================================
    // Fábrica de dados
    // =========================================================================

    private Mecanico fabricarMecanicoPadrao() {
        return new Mecanico(
                "Carlos Silva",
                "12345678909",
                "Motor",
                new BigDecimal("10.00")
        );
    }
}
