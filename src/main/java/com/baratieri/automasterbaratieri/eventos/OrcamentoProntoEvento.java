package com.baratieri.automasterbaratieri.eventos;

import com.baratieri.automasterbaratieri.entities.OrdemServico;

public record OrcamentoProntoEvento(Object source,OrdemServico os, String motivo) {
}
