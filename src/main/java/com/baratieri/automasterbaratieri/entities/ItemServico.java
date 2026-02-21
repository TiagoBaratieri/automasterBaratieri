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
public class ItemServico {

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
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @Column(name = "valor_cobrado", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCobrado;

    @Column(nullable = false)
    private Integer quantidade = 1;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "mecanico_id") // Pode ser null se a oficina não controlar por mecânico
    private Mecanico mecanicoResponsavel;

    public ItemServico(OrdemServico os, Servico servico, Mecanico mecanicoResponsavel,
                       BigDecimal valorCobrado, Integer quantidade, String observacao) {
        this.ordemServico = os;
        this.servico = servico;
        this.mecanicoResponsavel = mecanicoResponsavel;
        this.observacao = observacao;

        //Se não informou a quantidade, assume que é 1 serviço.
        this.quantidade = (quantidade != null) ? quantidade : 1;

        //  Se não informou o preço cobrado, puxa o valor base da tabela de serviços.
        this.valorCobrado = (valorCobrado != null) ? valorCobrado : servico.getValorMaoDeObraBase();
    }

    public BigDecimal getSubtotal() {
        if (valorCobrado == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        return valorCobrado.multiply(new BigDecimal(quantidade));
    }
}