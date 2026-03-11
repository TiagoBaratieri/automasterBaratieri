package com.baratieri.automasterbaratieri.entities;

import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.util.FormatacaoUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.baratieri.automasterbaratieri.services.util.ValidacaoDominio.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Peca extends Inativavel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String sku;

    private String nome;

    @Column(unique = true)
    private String partNumber;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String aplicacao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;


    @Column(precision = 10, scale = 2)
    private BigDecimal precoCusto;


    @Column(nullable = false)
    private Integer quantidadeEstoque;

    private Integer estoqueMinimo;

    public Peca(String sku, String nome, String partNumber, String marca,
                String aplicacao, BigDecimal precoVenda, BigDecimal precoCusto,
                Integer quantidadeEstoque, Integer estoqueMinimo) {
        validarDadosObrigatorio(nome, "O nome é obrigatório");
        validarDadosObrigatorio(marca, "A marca é obrigatória.");

        validarEstoqueNaoNegativo(quantidadeEstoque, "A quantidade em estoque não pode ser negativa.");

        this.sku = FormatacaoUtil.formatarTextoOpcional(sku);
        this.nome = FormatacaoUtil.formatarTextoOpcional(nome);
        this.partNumber = FormatacaoUtil.formatarTextoOpcional(partNumber);
        this.marca = FormatacaoUtil.formatarTextoOpcional(marca);
        this.aplicacao = FormatacaoUtil.formatarTextoOpcional(aplicacao);
        this.estoqueMinimo = estoqueMinimo;

        this.atualizarPeca(precoVenda, precoCusto, quantidadeEstoque);

    }

    public void atualizarPeca(BigDecimal precoVenda, BigDecimal precoCusto,Integer estoqueMinimo) {
        validarValorPositivo(precoVenda, "O preço de venda deve ser maior que zero.");
        validarValorNegativo(precoCusto, "O preço de custo não pode ser negativo.");
        validarEstoqueNaoNegativo(estoqueMinimo, "O estoque mínimo não pode ser negativo.");

        this.precoVenda = precoVenda;
        this.precoCusto = precoCusto;
        this.quantidadeEstoque = estoqueMinimo;
    }

    public void adicionarEstoque(Integer quantidade) {
        validarEstoqueNaoNegativo(quantidade,"Quantidade deve ser positiva.");
        this.quantidadeEstoque += quantidade;
    }

    public void baixarEstoque(Integer quantidade) {
        verificarQuantidadeEstoque(quantidade);
        this.quantidadeEstoque -= quantidade;
    }

    public boolean precisaReporEstoque() {
        return quantidadeEstoque <= estoqueMinimo;
    }

    private void verificarQuantidadeEstoque(Integer quantidade) {
        validarEstoqueNaoNegativo(quantidade,"Quantidade deve ser positiva.");
        if (quantidadeEstoque < quantidade) {
            throw new RegraNegocioException("Estoque insuficiente! Você tentou baixar " + quantidade
                    + " mas só existem " + quantidadeEstoque + " peças disponíveis.");
        }
    }

    @Version
    private Long version;
}