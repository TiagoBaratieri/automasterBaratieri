package com.baratieri.automasterbaratieri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.baratieri.automasterbaratieri.util.FormatacaoUtil.formatarTextoOpcional;
import static com.baratieri.automasterbaratieri.util.ValidacaoDominio.validarDadosObrigatorio;
import static com.baratieri.automasterbaratieri.util.ValidacaoDominio.validarValorNegativo;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servico extends Inativavel {

    @Id //
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorMaoDeObraBase;


    public void preencherDados(String descricao, BigDecimal valor) {
        validarDadosObrigatorio(descricao, "A descrição do serviço é obrigatória.");
        validarValorNegativo(valor, "O valor da mão de obra não pode ser negativo");
        this.descricao = formatarTextoOpcional(descricao);
        this.valorMaoDeObraBase = valor;
    }


}