package com.baratieri.automasterbaratieri.entities;

import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo extends Inativavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String placa;

    private String modelo;
    private String marca;
    private Integer ano;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public Veiculo(String placa, String modelo, String marca, Integer ano, Cliente cliente) {
        validarPlaca(placa);
        validarAno(ano);
        validarCliente(cliente);

        this.placa = formatarTextoOpcional(placa);
        this.modelo = formatarTextoOpcional(modelo);
        this.marca = formatarTextoOpcional(marca);
        this.ano = ano;
        this.cliente = cliente;
    }

    private void validarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new RegraNegocioException("A placa do veículo é obrigatória.");
        }
    }

    private void validarAno(Integer ano) {
        if (ano == null || ano <= 1900 || ano > Year.now().getValue() + 1) {
            throw new RegraNegocioException("Ano do veículo inválido.");
        }
    }

    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new RegraNegocioException("O cliente do veículo é obrigatório.");
        }
    }

    private String formatarTextoOpcional(String texto) {
        return texto != null ? texto.trim().toUpperCase() : null;
    }
}
