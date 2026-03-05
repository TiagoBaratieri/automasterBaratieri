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
        this.ativo = false;
    }

    public void ativar() {
        this.ativo = true;
    }
}