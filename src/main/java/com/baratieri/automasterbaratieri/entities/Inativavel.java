package com.baratieri.automasterbaratieri.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class Inativavel {

    @Column(nullable = false)
    private Boolean ativo = true;

    public void inativar() {
        ativo = false;
    }

    public void ativar() {
        ativo = true;
    }
}