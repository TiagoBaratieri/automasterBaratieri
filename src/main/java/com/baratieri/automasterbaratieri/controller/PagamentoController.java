package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.PagamentoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PagamentoResponseDTO;
import com.baratieri.automasterbaratieri.services.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordens-servico/{osId}/pagamento")
@RequiredArgsConstructor
public class PagamentoController {
    private final PagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> registrarPagamento(
            @PathVariable Long osId,
            @Valid @RequestBody PagamentoRequestDTO dto) {
        PagamentoResponseDTO responseDTO = pagamentoService.registrarPagamento(osId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);    }
}
