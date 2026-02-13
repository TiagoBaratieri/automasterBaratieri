package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.MecanicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.MecanicoResponseDTO;
import com.baratieri.automasterbaratieri.services.MecanicoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping(value = "/mecanicos")
@RequiredArgsConstructor
public class MecanicoController {

    private final MecanicoService mecanicoService;

    @PostMapping
    public ResponseEntity<MecanicoResponseDTO> salvar(@RequestBody @Valid MecanicoRequestDTO dto) {
        MecanicoResponseDTO mecanicoDto = mecanicoService.salvar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").build().toUri();
        return ResponseEntity.created(uri).body(mecanicoDto);
    }
}
