package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.*;
import com.baratieri.automasterbaratieri.dto.response.OrdemServicoResponseDTO;

import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.services.OrdemServicoService;
import com.baratieri.automasterbaratieri.services.RelatorioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping(value = "/ordens-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;
    private final RelatorioService relatorioService;


    @GetMapping("/{id}/relatorio")
    public ResponseEntity<byte[]> baixarRelatorioOrdemServico(@PathVariable Long id) {

        byte[] relatorioPdf = relatorioService.gerarPdfOrdemServico(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDispositionFormData("inline", "Ordem_Servico_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(relatorioPdf);
    }

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
                ordemServicoService.buscarOrdemServico(placa, status, pageable);
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
    public ResponseEntity<OrdemServicoResponseDTO> adicionarOrdemServico(
            @PathVariable Long id,
            @RequestBody @Valid ServicoPayloadDTO payload) {
        OrdemServicoResponseDTO responseDTO = ordemServicoService.adicionarServicoOrdemServico(id, payload);

        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/observacao")
    public ResponseEntity<OrdemServicoResponseDTO> atualizarDescricaoServico(
            @PathVariable Long id, @RequestBody
            @Valid AtualizarObservacaoOsRequestDTO dto) {
        OrdemServicoResponseDTO responseDTO = ordemServicoService.atualizarDescricaoServico(id, dto);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}/servicos/{idService}")
    public ResponseEntity<OrdemServicoResponseDTO> removerOrdemServico(
            @PathVariable Long id,
            @PathVariable Long idService) {
        return ResponseEntity.ok(ordemServicoService.removerServicoOrdemServico(id, idService));
    }

    @DeleteMapping("/{id}/pecas/{itemPecaId}")
    public ResponseEntity<OrdemServicoResponseDTO> removerPeca(
            @PathVariable Long id,
            @PathVariable Long itemPecaId) {
        return ResponseEntity.ok(ordemServicoService.removerPecaOrdemServico(id, itemPecaId));
    }


    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<OrdemServicoResponseDTO> aprovarOrdemServico(@PathVariable Long id) {
        OrdemServicoResponseDTO responseDTO = ordemServicoService.aprovarOrdemServico(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{id}/enviar-orcamento")
    public ResponseEntity<Void> enviarOrcamentoParaCliente(@PathVariable Long id,
                                                           @RequestParam(required = false)String motivo) {
        ordemServicoService.enviarOrcamento(id, motivo);
        return ResponseEntity.accepted().build();
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