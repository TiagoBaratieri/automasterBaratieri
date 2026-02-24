package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.ServicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ServicoResponseDTO;
import com.baratieri.automasterbaratieri.services.MaoDeObraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class MaoDeObraController {

    private final MaoDeObraService maoDeObraService;

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> buscarServicoPorId(@PathVariable Long id) {
        ServicoResponseDTO responseDTO = maoDeObraService.buscarServicoPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ServicoResponseDTO>> listarServicos(
            @RequestParam(required = false) String descricao,
            @PageableDefault(size = 10, page = 0, sort = "descricao") Pageable pageable) {

        Page<ServicoResponseDTO> servicosPage = maoDeObraService.pesquisarServicos(descricao, pageable);
        return ResponseEntity.ok(servicosPage);
    }

    @PostMapping
    public ResponseEntity<ServicoResponseDTO> salvarMaoDeObra(@RequestBody @Valid
                                                              ServicoRequestDTO dto,
                                                              UriComponentsBuilder uriBuilder) {

        ServicoResponseDTO servicoDto = maoDeObraService.salvarMaoDeObra(dto);

        URI uri = uriBuilder.path("/{id}")
                .buildAndExpand(servicoDto.id())
                .toUri();
        return ResponseEntity.created(uri).body(servicoDto);
    }


}
