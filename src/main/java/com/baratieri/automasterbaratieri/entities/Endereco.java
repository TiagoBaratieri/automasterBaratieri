package com.baratieri.automasterbaratieri.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Endereco {
    @Column(length = 150) // Limita tamanho
    private String logradouro;

    @Column(length = 10)
    private String numero;

    private String complemento;

    @Column(length = 100)
    private String bairro;

    @Column(length = 9) // 00000-000
    private String cep;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2) // PR, SP, SC
    private String estado;
}