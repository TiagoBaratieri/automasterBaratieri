package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.VeiculoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.VeiculoResponseDTO;
import com.baratieri.automasterbaratieri.services.VeiculoService;
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
@RequestMapping(value = "veiculos")
@RequiredArgsConstructor
public class VeiculoController {

    private final VeiculoService veiculoService;

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorId(@PathVariable Long id) {
        VeiculoResponseDTO responseDTO = veiculoService.buscarVeiculoPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorPlaca(@PathVariable @Valid String placa) {
        VeiculoResponseDTO response = veiculoService.buscarVeiculo(placa);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<VeiculoResponseDTO>> listarVeiculos(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String placa,
            @PageableDefault(size = 10, page = 0, sort = "modelo")Pageable pageable) {
        Page<VeiculoResponseDTO> veiculosPage = veiculoService.listarVeiculos(clienteId, placa, pageable);
        return ResponseEntity.ok(veiculosPage);
    }
    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> salvarVeiculo(
            @RequestBody @Valid VeiculoRequestDTO dto) {
        VeiculoResponseDTO veiculoDto = veiculoService.salvarVeiculo(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(veiculoDto.id()).toUri();
        return ResponseEntity.created(uri).body(veiculoDto);

    }
}
