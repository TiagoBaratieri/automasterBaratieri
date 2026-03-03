package com.baratieri.automasterbaratieri.services.util;

import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;

import java.math.BigDecimal;

public class ValidadorUtil {
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

    public static void validarStatusMecanico(Boolean status, String mensagemErro) {
        if (status == null || !status) {
            throw new RegraNegocioException(mensagemErro);

        }
    }

    public static void validarStatusOrdemServicoFinalizadaOuCancelada(StatusOS status, String mensagemErro) {
        if (status == StatusOS.FINALIZADO || status == StatusOS.CANCELADO) {
            throw new RegraNegocioException(mensagemErro);
        }
    }


    public static void validarStatusOrdemServicoFinalizado(StatusOS status, String mensagemErro) {
        if (status == StatusOS.FINALIZADO)  {
            throw new RegraNegocioException(mensagemErro);
        }
    }

    public static void validarStatusOrdemServicoOrcamento(StatusOS status, String mensagemErro) {
        if (status != StatusOS.ORCAMENTO)  {
            throw new RegraNegocioException(mensagemErro);
        }
    }

    public static void validarStatusOrdemServicoEmExecucao(StatusOS status, String mensagemErro) {
        if (status != StatusOS.EM_EXECUCAO)  {
            throw new RegraNegocioException(mensagemErro);
        }
    }


}


