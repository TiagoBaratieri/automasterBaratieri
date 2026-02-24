package com.baratieri.automasterbaratieri.dto.response;

import com.baratieri.automasterbaratieri.entities.Mecanico;

import java.math.BigDecimal;

public record MecanicoResponseDTO(
        String nome,
        String cpf,
        String especialidade,
        BigDecimal taxaComissao,
        Boolean ativo) {

    public static MecanicoResponseDTO fromEntity(Mecanico mecanico) {
        if (mecanico == null) return null;
        return new MecanicoResponseDTO(
                mecanico.getNome(),
                mecanico.getCpf(),
                mecanico.getEspecialidade(),
                mecanico.getTaxaComissao(),
                mecanico.getAtivo()

        );
    }
}
