package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.AberturaOsRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.OrdemServicoResponseDTO;
import com.baratieri.automasterbaratieri.services.OrdemServicoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/abrir-os")
@AllArgsConstructor
public class OrdemServicoController {

    private OrdemServicoService ordemServicoService;

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> abrirOrdemServico(
            @RequestBody @Valid AberturaOsRequestDTO dto) {
        OrdemServicoResponseDTO responseDTO = ordemServicoService.abrirOdemServico(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }
}
