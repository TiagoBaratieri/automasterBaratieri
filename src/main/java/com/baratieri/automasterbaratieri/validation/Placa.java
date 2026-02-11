package com.baratieri.automasterbaratieri.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation; // <--- Importe isso
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// REGRAS DE COMPOSIÇÃO
@NotBlank(message = "A placa é obrigatória")
// Regex ajustado para aceitar Hífen opcional (Ex: ABC-1234 ou ABC1234)
// O '?' depois do '-' significa que ele pode ou não existir.
@Pattern(regexp = "[A-Z]{3}-?[0-9][0-9A-Z][0-9]{2}", message = "A placa deve seguir o padrão (ex: ABC-1234 ou ABC1D23)")
@ReportAsSingleViolation // <--- O Pulo do Gato: Unifica o erro
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Placa {

    // A mensagem padrão caso o usuário não defina uma
    String message() default "Placa inválida! O formato deve ser Mercosul ou Antigo.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}