package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.ServicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ServicoResponseDTO;
import com.baratieri.automasterbaratieri.services.MaoDeObraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class MaoDeObraController {

    private final MaoDeObraService maoDeObraService;

    @PostMapping
    public ResponseEntity<ServicoResponseDTO> salvar(@Valid @RequestBody  ServicoRequestDTO dto,
                                                        UriComponentsBuilder uriBuilder) {

        ServicoResponseDTO servicoDto = maoDeObraService.salvar(dto);

        URI uri = uriBuilder.path("/{id}")
                .buildAndExpand(servicoDto.id())
                .toUri();
        return ResponseEntity.created(uri).body(servicoDto);
    }
}
