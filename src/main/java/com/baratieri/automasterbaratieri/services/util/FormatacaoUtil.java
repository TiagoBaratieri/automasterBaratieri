package com.baratieri.automasterbaratieri.services.util;

import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Predicate;

// O Lombok cria o construtor privado automaticamente por debaixo dos panos!
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FormatacaoUtil {

    public static String limparPlaca(String placa) {
        if (placa == null) return null;

        // O [^a-zA-Z0-9] significa: "Apague TUDO que NÃO for letra (a-z) ou número (0-9)"
        // Depois, transforma tudo que sobrou em maiúsculo.
        return placa.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    public static String limparDocumento(String documento){
        if (documento == null) return null;
        return documento.replaceAll("\\D", "");
    }

    public static void validarDocumentoUnico(String docLimpo, String docOriginal,
                                             String nomeEntidade, Predicate<String> verificadorBanco){
        if (verificadorBanco.test(docLimpo)){
            throw new RegraNegocioException("Já existe um(a) " + nomeEntidade +
                    " cadastrado(a) com este documento: " + docOriginal);
        }
    }
}
