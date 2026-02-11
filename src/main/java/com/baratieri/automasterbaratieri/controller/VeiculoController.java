package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.VeiculoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.VeiculoResponseDTO;
import com.baratieri.automasterbaratieri.services.VeiculoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping(value = "veiculos")
@AllArgsConstructor
public class VeiculoController {

    private VeiculoService veiculoService;

    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> salvar(
            @RequestBody @Valid VeiculoRequestDTO dto) {
        VeiculoResponseDTO veiculoDto = veiculoService.salvar(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(veiculoDto.id()).toUri();
        return ResponseEntity.created(uri).body(veiculoDto);

    }
}
