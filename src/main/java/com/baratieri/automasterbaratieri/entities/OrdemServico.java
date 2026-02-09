package com.baratieri.automasterbaratieri.entities;

import com.baratieri.automasterbaratieri.enums.StatusOS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Veiculo veiculo;

    @Enumerated(EnumType.STRING)
    private StatusOS status;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemPeca> itensPeca = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemServico> itensServico = new ArrayList<>();

    public void calcularTotal() {
        BigDecimal totalPecas = BigDecimal.ZERO;

        if (itensPeca != null && !itensPeca.isEmpty()) {
            totalPecas = itensPeca.stream()
                    .map(ItemPeca::getSubtotal) // Chama o getSubtotal() de cada item
                    .filter(Objects::nonNull)  // Segurança contra nulos
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma acumulada
        }

        // 2. Soma o total dos SERVIÇOS
        BigDecimal totalServicos = BigDecimal.ZERO;

        if (itensServico != null && !itensServico.isEmpty()) {
            totalServicos = itensServico.stream()
                    .map(ItemServico::getSubtotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // 3. Atualiza o atributo da própria classe
        this.valorTotal = totalPecas.add(totalServicos);
    }

    @PrePersist
    @PreUpdate
    public void garantirCalculoAntesDeSalvar() {
        this.calcularTotal();
    }
}
