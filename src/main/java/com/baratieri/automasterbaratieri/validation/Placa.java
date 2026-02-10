package com.baratieri.automasterbaratieri.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 1. Combinação de regras (Composição)
@NotBlank(message = "A placa é obrigatória")
@Pattern(regexp = "[A-Z]{3}[0-9][0-9A-Z][0-9]{2}", message = "A placa deve seguir o padrão (ex: ABC1234 ou ABC1D23)")
// 2. Configurações do Bean Validation
@Constraint(validatedBy = {}) // Não precisa de classe lógica extra, pois já usamos @Pattern
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Onde pode usar (Campos e Parametros)
@Retention(RetentionPolicy.RUNTIME)
public @interface Placa {

    String message() default "Placa inválida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}