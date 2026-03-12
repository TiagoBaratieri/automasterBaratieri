package com.baratieri.automasterbaratieri.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.baratieri.automasterbaratieri.util.ValidacaoDominio.validarValorMinimo;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("DINHEIRO")
public class PagamentoDinheiro extends Pagamento {

    private BigDecimal valorRecebido;
    private BigDecimal troco;

    public PagamentoDinheiro(OrdemServico os, BigDecimal valorCobrado, BigDecimal valorRecebido) {
        super(valorCobrado, os);
        validarValorMinimo(valorCobrado,valorRecebido, "Para pagamentos em dinheiro, o valor recebido " +
                "não pode ser menor que o valor cobrado.");
        this.valorRecebido = valorRecebido;
        this.troco = valorRecebido.subtract(valorCobrado);
    }
}