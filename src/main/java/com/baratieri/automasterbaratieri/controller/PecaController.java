package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.PecaRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PecaResponseDTO;
import com.baratieri.automasterbaratieri.services.PecaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@Controller
@RequestMapping(value = "/pecas")
@RequiredArgsConstructor
public class PecaController {

    private final PecaService pecaService;

    @GetMapping("/{id}")
    public ResponseEntity<PecaResponseDTO> buscarPecaPorId(@PathVariable Long id) {
        PecaResponseDTO responseDTO = pecaService.buscarPecaPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<PecaResponseDTO>> pesquisarEstoque(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String aplicacao,

            @PageableDefault(size = 10, page = 0, sort = "nome") Pageable pageable) {

        Page<PecaResponseDTO> pecasDTO = pecaService.buscarPecas(nome, marca, aplicacao, pageable);

        return ResponseEntity.ok(pecasDTO);
    }

    @PostMapping
    public ResponseEntity<PecaResponseDTO> salvarPeca(
            @RequestBody @Valid PecaRequestDTO pecaRequestDTO) {
        PecaResponseDTO pecaDto = pecaService.salvarPecas(pecaRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").build().toUri();
        return ResponseEntity.created(uri).body(pecaDto);
    }
}

