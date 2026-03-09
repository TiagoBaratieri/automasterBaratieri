package com.baratieri.automasterbaratieri.services.util;

import com.baratieri.automasterbaratieri.entities.ItemPeca;
import com.baratieri.automasterbaratieri.entities.ItemServico;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;

import java.math.BigDecimal;
import java.util.List;

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


    public static void validarStatusOrdemServicoOrcamento(StatusOS status, String mensagemErro) {
        if (status != StatusOS.AGUARDANDO_APROVACAO)  {
            throw new RegraNegocioException(mensagemErro);
        }
    }

    public static void validarStatusOrdemServicoEmExecucao(StatusOS status, String mensagemErro) {
        if (status != StatusOS.EM_EXECUCAO)  {
            throw new RegraNegocioException(mensagemErro);
        }
    }

    public static void validarExistePecaOrdemServico(List<ItemServico> servicos, List<ItemPeca> pecas, String mensagemErro) {
        boolean existePeca = pecas == null || pecas.isEmpty();
        boolean existeServico = servicos == null || servicos.isEmpty();
        if (existePeca && existeServico) {
            throw new RegraNegocioException(mensagemErro);
        }
    }

}


