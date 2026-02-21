package com.baratieri.automasterbaratieri.services.util;

import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Predicate;

// O Lombok cria o construtor privado automaticamente por debaixo dos panos!
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentoUtil {

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
