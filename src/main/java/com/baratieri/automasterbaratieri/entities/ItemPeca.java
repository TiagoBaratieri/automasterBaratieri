package com.baratieri.automasterbaratieri.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemPeca {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private OrdemServico ordemServico;

    @ManyToOne
    @JoinColumn(name = "peca_id", nullable = false)
    private Peca peca;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    public ItemPeca(OrdemServico ordemServico, Peca peca,
                    Integer quantidade, BigDecimal valorUnitarioCobrado) {
        this.ordemServico = ordemServico;
        this.peca = peca;
        this.quantidade = quantidade;
        this.precoUnitario = (valorUnitarioCobrado != null ?
                valorUnitarioCobrado : peca.getPrecoVenda());
    }

    public BigDecimal getSubtotal() {
        if (precoUnitario == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        return precoUnitario.multiply(new BigDecimal(quantidade));
    }
}