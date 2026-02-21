package com.baratieri.automasterbaratieri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


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
        this.nome = nome;
        this.cpfOuCnpj = cpfOuCnpj;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
    }
}
