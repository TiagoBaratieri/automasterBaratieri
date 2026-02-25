package com.baratieri.automasterbaratieri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.baratieri.automasterbaratieri.services.util.FormatacaoUtil.formatarEmail;
import static com.baratieri.automasterbaratieri.services.util.FormatacaoUtil.formatarTextoOpcional;
import static com.baratieri.automasterbaratieri.services.util.ValidadorUtil.validarDadosObrigatorio;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpfOuCnpj;

    private String telefone;

    private String email;

    @Embedded
    private Endereco endereco;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Veiculo> veiculos = new ArrayList<>();

    public Cliente(String nome, String cpfOuCnpj, Endereco endereco, String telefone, String email) {

        validarDadosObrigatorio(nome, "O nome do cliente é obrigatório.");
        validarDadosObrigatorio(cpfOuCnpj, "O CPF ou CNPJ é obrigatório.");

        this.nome = nome.trim().toUpperCase();
        this.cpfOuCnpj = cpfOuCnpj;

        this.atualizarDadosDeContato(endereco, telefone, email);
    }
    public void atualizarDadosDeContato(Endereco endereco, String telefone, String email) {

        this.endereco = endereco;
        this.telefone = formatarTextoOpcional(telefone);

        this.email = formatarEmail(email);
    }
}


