package com.baratieri.automasterbaratieri.dto.response;

import com.baratieri.automasterbaratieri.entities.Veiculo;

public record VeiculoResponseDTO (Long id,
                                  String palca,
                                  String modelo,
                                  String marca,
                                  Integer ano){

    public static VeiculoResponseDTO fromEntity(Veiculo veiculo){
        if (veiculo == null) return null;

        return new VeiculoResponseDTO(
                veiculo.getId(),
                veiculo.getPlaca(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAno()
        );
    }
}
