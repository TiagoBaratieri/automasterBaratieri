package com.baratieri.automasterbaratieri.domain;

import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.entities.Veiculo;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VeiculoTest {

    @Mock
    private Cliente clienteMock;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        veiculo = fabricarVeiculoPadrao();
    }

    // =========================================================================
    // Construtor — formatação aplicada pela entidade
    // =========================================================================

    @Test
    @DisplayName("Deve criar veículo com placa formatada em maiúsculo e sem espaços")
    void deveCriarVeiculoComPlacaFormatada() {
        // ARRANGE & ACT
        Veiculo veiculoPlacaSujа =fabricarVeiculoPadrao();

        // ASSERT
        assertEquals("BRA2E19", veiculoPlacaSujа.getPlaca());
    }

    @Test
    @DisplayName("Deve criar veículo com marca formatada em maiúsculo")
    void deveCriarVeiculoComMarcaEmMaiusculo() {
        assertEquals("TESLA", veiculo.getMarca());
    }

    @Test
    @DisplayName("Deve criar veículo com modelo formatado em maiúsculo")
    void deveCriarVeiculoComModeloEmMaiusculo() {
        assertEquals("MODELO Y", veiculo.getModelo());
    }


    // =========================================================================
    // Construtor — validações de domínio
    // =========================================================================

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar veículo com placa nula")
    void deveLancarExcecaoAoCriarVeiculoComPlacaNula() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Veiculo(null, "Modelo Y", "Tesla", 2023, clienteMock));

        assertEquals("A placa do veículo é obrigatória.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar veículo com placa em branco")
    void deveLancarExcecaoAoCriarVeiculoComPlacaEmBranco() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Veiculo("   ", "Modelo Y", "Tesla", 2023, clienteMock));

        assertEquals("A placa do veículo é obrigatória.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar veículo com ano nulo")
    void deveLancarExcecaoAoCriarVeiculoComAnoNulo() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Veiculo("BRA2E19", "Modelo Y", "Tesla", null, clienteMock));

        assertEquals("Ano do veículo inválido.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar veículo com ano no futuro")
    void deveLancarExcecaoAoCriarVeiculoComAnoNoFuturo() {
        int anoFuturo = Year.now().getValue() + 2;
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Veiculo("BRA2E19", "Modelo Y", "Tesla", anoFuturo, clienteMock));

        assertEquals("Ano do veículo inválido.", excecao.getMessage());
    }
    
    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar veículo com ano muito antigo")
    void deveLancarExcecaoAoCriarVeiculoComAnoMuitoAntigo() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Veiculo("BRA2E19", "Modelo Y", "Tesla", 1899, clienteMock));

        assertEquals("Ano do veículo inválido.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar veículo com cliente nulo")
    void deveLancarExcecaoAoCriarVeiculoComClienteNulo() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Veiculo("BRA2E19", "Modelo Y", "Tesla", 2023, null));

        assertEquals("O cliente do veículo é obrigatório.", excecao.getMessage());
    }

    // =========================================================================
    // Fábrica de dados
    // =========================================================================

    private Veiculo fabricarVeiculoPadrao() {
        return new Veiculo(
                "bra2e19",
                "modelo y",
                "tesla",
                2023,
                clienteMock
        );
    }
}
