package com.baratieri.automasterbaratieri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String sku;

    @Column(nullable = false)
    private String nome;

    private String partNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;


    @Column(precision = 10, scale = 2)
    private BigDecimal precoCusto;


    @Column(nullable = false)
    private Integer quantidadeEstoque = 0;

    private Integer estoqueMinimo = 5;

    public void adicionarEstoque(Integer quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
        this.quantidadeEstoque+= quantidade;
    }

    public void baixarEstoque(Integer quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
        this.quantidadeEstoque -= quantidade;
    }

    public boolean precisaReporEstoque() {
        return this.quantidadeEstoque <= this.estoqueMinimo;
    }
    @Version
    private Long version;
}