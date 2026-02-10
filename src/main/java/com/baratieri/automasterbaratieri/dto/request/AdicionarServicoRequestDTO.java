package com.baratieri.automasterbaratieri.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdicionarServicoRequestDTO(@NotNull(message = "O ID do serviço é obrigatório")
                                      Long servicoId,

                                      @Positive(message = "A quantidade deve ser maior que zero")
                                      Integer quantidade,
                                      String observacao)
{

}




