package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.ServicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ServicoResponseDTO;
import com.baratieri.automasterbaratieri.services.ServicoService;
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
public class ServicoController {

    private final ServicoService service;

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> buscarServicoPorId(@PathVariable Long id) {
        ServicoResponseDTO responseDTO = service.buscarServicoPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ServicoResponseDTO>> buscarServico(
            @RequestParam(required = false) String descricao,
            @PageableDefault(size = 10, page = 0, sort = "descricao") Pageable pageable) {

        Page<ServicoResponseDTO> servicosPage = service.buscarServicos(descricao, pageable);
        return ResponseEntity.ok(servicosPage);
    }

    @PostMapping
    public ResponseEntity<ServicoResponseDTO> salvarServico(@RequestBody @Valid
                                                              ServicoRequestDTO dto,
                                                              UriComponentsBuilder uriBuilder) {

        System.out.println("DTO RECEBIDO DO POSTMAN: " + dto);
        ServicoResponseDTO servicoDto = service.salvarServico(dto);

        URI uri = uriBuilder.path("/{id}")
                .buildAndExpand(servicoDto.id())
                .toUri();
        return ResponseEntity.created(uri).body(servicoDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizarServico(
            @PathVariable Long id,
            @Valid @RequestBody ServicoRequestDTO dto) {

        ServicoResponseDTO servicoAtualizado = service.atualizarServico(id, dto);
        return ResponseEntity.ok(servicoAtualizado);
    }

}
