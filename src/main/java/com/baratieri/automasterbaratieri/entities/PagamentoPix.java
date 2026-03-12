package com.baratieri.automasterbaratieri.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.baratieri.automasterbaratieri.util.ValidacaoDominio.validarDadosObrigatorio;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("PIX")
public class PagamentoPix extends Pagamento{
    private String  chavePixRecebedor;
    private String  txid; // Código único da transferência PIX

    public PagamentoPix(OrdemServico os, BigDecimal valor, String chavePixRecebedor) {
        super(valor, os);
        validarDadosObrigatorio(chavePixRecebedor,
                "Para pagamentos via PIX, a chave PIX é obrigatória.");
        this.chavePixRecebedor = chavePixRecebedor;
    }


}
