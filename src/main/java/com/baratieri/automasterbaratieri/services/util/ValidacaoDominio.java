package com.baratieri.automasterbaratieri.services.util;

import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;

import java.math.BigDecimal;

public class ValidacaoDominio {
    public static void validarDadosObrigatorio(String valor, String mensssagemErro) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new RegraNegocioException(mensssagemErro);
        }
    }

    public static void validarValorPositivo(BigDecimal valor, String menssagemErro) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException(menssagemErro);
        }
    }

    public static void validarEstoqueNaoNegativo(Integer valor, String mensagemErro) {
        if (valor == null || valor <= 0) {
            throw new RegraNegocioException(mensagemErro);
        }
    }

    public static void validarValorNegativo(BigDecimal precoCusto, String mensagemErro) {
        if (precoCusto == null || precoCusto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException(mensagemErro);
        }
    }

}