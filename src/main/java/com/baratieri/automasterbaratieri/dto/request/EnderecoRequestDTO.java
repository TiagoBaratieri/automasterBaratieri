package com.baratieri.automasterbaratieri.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoRequestDTO(
        @NotBlank(message = "O logradouro é obrigatório")
        String logradouro,

        @NotBlank(message = "O número é obrigatório")
        String numero,

        String complemento, // Opcional

        @NotBlank(message = "O bairro é obrigatório")
        String bairro,

        @NotBlank(message = "O CEP é obrigatório")
        @Pattern(regexp = "\\d{8}", message = "O CEP deve conter 8 números")
        String cep,

        @NotBlank(message = "A cidade é obrigatória")
        String cidade,

        @NotBlank(message = "O estado (UF) é obrigatório")
        @Pattern(regexp = "[A-Z]{2}", message = "O estado deve ser a sigla (ex: PR, SP)")
        String estado
) {}
