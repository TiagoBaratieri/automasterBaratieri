package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.*;
import com.baratieri.automasterbaratieri.dto.response.OrdemServicoResponseDTO;

import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.services.OrdemServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping(value = "/ordens-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;


    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarOrdemServicoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.buscarOrdemServicoPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrdemServicoResponseDTO>> buscarOrdemServico(
            @RequestParam(name = "placa", required = false) String placa,
            @RequestParam(name = "status", required = false) StatusOS status,
            @PageableDefault(size = 10, page = 0, sort = "dataAbertura", direction =
                    Sort.Direction.DESC) Pageable pageable) {

        Page<OrdemServicoResponseDTO> ordemServicosDTO =
                ordemServicoService.buscarOrdemServico(placa, status,pageable);
        return ResponseEntity.ok(ordemServicosDTO);
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
    public ResponseEntity<OrdemServicoResponseDTO> adicionarPecaOrdemServico(
            @PathVariable Long id,
            @RequestBody @Valid PecaPayloadDTO payloadDTO) {
        OrdemServicoResponseDTO responseDTO =
                ordemServicoService.adicionarPecaOrdemServico(id, payloadDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{id}/servicos")
    public ResponseEntity<OrdemServicoResponseDTO> adicionarMaoDeObraOrdemServico(
            @PathVariable Long id,
            @RequestBody @Valid ServicoPayloadDTO payload) {
        OrdemServicoResponseDTO responseDTO = ordemServicoService.adicionarMaoDeObraOrdemServico(id, payload);

        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<OrdemServicoResponseDTO> aprovarOrdemServico(@PathVariable Long id) {
        OrdemServicoResponseDTO responseDTO = ordemServicoService.aprovarOrdemServico(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<OrdemServicoResponseDTO> finalizar(@PathVariable Long id) {
        OrdemServicoResponseDTO osFinalizada = ordemServicoService.finalizarOrdemServico(id);

        return ResponseEntity.ok(osFinalizada);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<OrdemServicoResponseDTO> cancelarOrdemServico(@PathVariable Long id) {
        OrdemServicoResponseDTO dto = ordemServicoService.cancelarOrdemServico(id);
        return ResponseEntity.ok(dto);
    }

}