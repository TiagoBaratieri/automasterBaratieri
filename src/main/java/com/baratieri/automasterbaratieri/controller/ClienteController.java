package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.ClienteAtualizacaoRequestDTO;
import com.baratieri.automasterbaratieri.dto.request.ClienteRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ClienteResponseDTO;
import com.baratieri.automasterbaratieri.services.ClienteService;
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
@RequestMapping(value = "/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarClientePorId(@PathVariable Long id) {
        ClienteResponseDTO responseDTO = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listarClientes(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpfOuCnpj,
            @PageableDefault(size = 10, page = 0, sort = "nome")Pageable pageable) {

        Page<ClienteResponseDTO> clientePage = clienteService.listarClientes(nome, cpfOuCnpj, pageable);
        return ResponseEntity.ok().body(clientePage);
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> salvarCliente(
            @RequestBody @Valid ClienteRequestDTO dto) {
        ClienteResponseDTO clienteDto = clienteService.salvarCliente(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(clienteDto.id()).toUri();
        return ResponseEntity.created(uri).body(clienteDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Long id,
                                                               @Valid @RequestBody ClienteAtualizacaoRequestDTO dto) {
        ClienteResponseDTO clienteDto = clienteService.atualizarCliente(id, dto);
        return ResponseEntity.ok().body(clienteDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCliente(@PathVariable Long id) {
        clienteService.excluirCliente(id);
        return ResponseEntity.noContent().build();
    }
}
