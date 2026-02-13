package com.baratieri.automasterbaratieri.dto.response;


import com.baratieri.automasterbaratieri.entities.Servico;

import java.math.BigDecimal;

public record ServicoResponseDTO(
        Long id,
        String descricao,
        BigDecimal valorMaoDeObraBase) {

    public static ServicoResponseDTO fromEntity(Servico servico) {
        return new ServicoResponseDTO(
                servico.getId(),
                servico.getDescricao(),
                servico.getValorMaoDeObraBase()
        );
    }
}
