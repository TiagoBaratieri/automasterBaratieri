package com.baratieri.automasterbaratieri.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.baratieri.automasterbaratieri.util.ValidacaoDominio.validarDadosObrigatorio;
import static com.baratieri.automasterbaratieri.util.ValidacaoDominio.validarInteiroMaiorQueZero;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CARTAO")
public class PagamentoCartao extends Pagamento{
    private Integer numeroParcelas;
    private String bandeira;
    private String nsuTransacao; // Código do recibo da maquininha

    public PagamentoCartao(OrdemServico ordemServico, BigDecimal valor, Integer numeroParcelas,
                           String bandeira) {
        super(valor, ordemServico);
        validarInteiroMaiorQueZero(numeroParcelas, "O número de parcelas deve ser maior que zero.");
        validarDadosObrigatorio(bandeira, "A bandeira do cartão é obrigatória.");
        this.numeroParcelas = numeroParcelas;
        this.bandeira = bandeira;

    }
}
