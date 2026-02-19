package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.*;
import com.baratieri.automasterbaratieri.dto.response.OrdemServicoResponseDTO;

import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.services.OrdemServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/ordens-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;


    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<OrdemServicoResponseDTO>> buscar(
            @RequestParam(name = "placa", required = false) String placa,
            @RequestParam(name = "status", required = false) StatusOS status) {

        // DICA DE OURO: Coloque este print temporário aqui!
        System.out.println("Placa que chegou no Controller: " + placa);

        List<OrdemServicoResponseDTO> ordemServicos = ordemServicoService.buscarTodosOrdemServico(placa, status);
        return ResponseEntity.ok(ordemServicos);
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> abrirOrdemServico(
            @RequestBody @Valid AberturaOsRequestDTO dto) {

        OrdemServicoResponseDTO responseDTO = ordemServicoService.abrirOdemServico(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(responseDTO.id()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PostMapping("/{id}/pecas")
    public ResponseEntity<OrdemServicoResponseDTO> adicionarPeca(
            @PathVariable Long id,
            @RequestBody @Valid AdicionarPecaPayloadDTO payloadDTO) {

        ItemPecaRequestDTO itemDto = new ItemPecaRequestDTO(
                id,
                payloadDTO.pecaId(),
                payloadDTO.quantidade(),
                payloadDTO.valorUnitario()
        );

        return ResponseEntity.ok(ordemServicoService.adicionarPecaOrdemServico(itemDto));
    }

    @PostMapping("/{id}/servicos")
    public ResponseEntity<OrdemServicoResponseDTO> adicionarServico(
            @PathVariable Long id,
            @RequestBody @Valid AdicionarServicoPayloadDTO payload) {

        ItemServicoRequestDTO dtoInterno = new ItemServicoRequestDTO(
                id,
                payload.servicoId(),
                payload.mecanicoId(),
                payload.valorCobrado(),
                payload.quantidade(),
                payload.observacao()
        );

        return ResponseEntity.ok(ordemServicoService.lancarServico(dtoInterno));
    }

    @PutMapping("/{id}/aprovar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void aprovarOrcamento(@PathVariable Long id) {
        ordemServicoService.aprovarOrcamento(id);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<OrdemServicoResponseDTO> finalizar(@PathVariable Long id) {
        OrdemServicoResponseDTO osFinalizada = ordemServicoService.finalizarOrdemServico(id);

        return ResponseEntity.ok(osFinalizada);
    }

}