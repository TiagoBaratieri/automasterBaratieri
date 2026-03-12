package com.baratieri.automasterbaratieri.util;

import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;

import java.math.BigDecimal;

public final class ValidacaoDominio {

    private ValidacaoDominio() {
    }

    public static void validarDadosObrigatorio(String valor, String mensssagemErro) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new RegraNegocioException(mensssagemErro);
        }

    }
    public static void validarValorMaiorQueZero(BigDecimal valor, String menssagemErro) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException(menssagemErro);
        }
    }

    public static void validarInteiroMaiorQueZero(Integer valor, String mensagemErro) {
        if (valor == null || valor <= 0) {
            throw new RegraNegocioException(mensagemErro);
        }
    }

    public static void validarValorNegativo(BigDecimal valor, String mensagemErro) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException(mensagemErro);
        }
    }

    public static void validarValorMinimo(BigDecimal valorCobrado,
                                          BigDecimal valorrecebido, String mensagemErro) {
        if(valorrecebido == null || valorrecebido.compareTo(valorCobrado) < 0) {
            throw new RegraNegocioException(mensagemErro);
        }
    }

}