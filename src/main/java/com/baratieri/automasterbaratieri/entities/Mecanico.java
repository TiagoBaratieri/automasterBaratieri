package com.baratieri.automasterbaratieri.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.baratieri.automasterbaratieri.util.FormatacaoUtil.formatarTextoOpcional;
import static com.baratieri.automasterbaratieri.util.ValidacaoDominio.validarDadosObrigatorio;
import static com.baratieri.automasterbaratieri.util.ValidacaoDominio.validarValorMaiorQueZero;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mecanico extends Inativavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    private String especialidade;


    @Column(precision = 5, scale = 2)
    private BigDecimal taxaComissao;


    public Mecanico(String nome, String cpf, String especialidade,
                    BigDecimal taxaComissao) {

        validarDadosObrigatorio(nome, "O nome do mecânico é obrigatório.");
        validarDadosObrigatorio(cpf, "O CPF do mecânico é obrigatório.");
        validarDadosObrigatorio(especialidade,"A especialidade do mecânico é obrigatória.");
        this.nome = formatarTextoOpcional(nome);
        this.cpf = formatarTextoOpcional(cpf);
        this.especialidade = formatarTextoOpcional(especialidade);
        atualizarDados(taxaComissao);

    }

    public void atualizarDados(BigDecimal taxaComissao){
        validarValorMaiorQueZero(taxaComissao, "A taxa de comissão não pode ser nula. Ou negativa.");
        this.taxaComissao = taxaComissao;
    }


}