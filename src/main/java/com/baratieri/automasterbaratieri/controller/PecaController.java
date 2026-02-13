package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.PecaRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PecaResponseDTO;
import com.baratieri.automasterbaratieri.services.PecaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping(value = "/pecas")
@RequiredArgsConstructor
public class PecaController {

    private final PecaService pecaService;

    @PostMapping
    public ResponseEntity<PecaResponseDTO> salvar(
            @RequestBody @Valid PecaRequestDTO pecaRequestDTO) {
        PecaResponseDTO pecaDto = pecaService.salvar(pecaRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").build().toUri();
        return ResponseEntity.created(uri).body(pecaDto);
    }
}
