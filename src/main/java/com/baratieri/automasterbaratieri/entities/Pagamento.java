package com.baratieri.automasterbaratieri.entities;

import com.baratieri.automasterbaratieri.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_pagamento", discriminatorType = DiscriminatorType.STRING)
public abstract class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataPagamento;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento statusPagamento;
    @Column(nullable = false)
    private BigDecimal valor;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    public Pagamento(BigDecimal valor, OrdemServico ordemServico) {
        this.valor = valor;
        this.ordemServico = ordemServico;
        this.statusPagamento = StatusPagamento.PENDENTE;
    }

    public void confirmarPagamento() {
        validarStatusPagamento();
        if (statusPagamento == StatusPagamento.PAGO) {
            throw new RuntimeException("Este pagamento já foi processado anteriormente.");
        }
        statusPagamento = StatusPagamento.PAGO;
        dataPagamento = LocalDateTime.now();
    }

    public void validarStatusPagamento() {
        if (statusPagamento == null) {
            throw new RuntimeException("O status do pagamento não pode ser nulo.");
        }
    }
}
