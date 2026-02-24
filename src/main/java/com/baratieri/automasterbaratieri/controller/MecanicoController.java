package com.baratieri.automasterbaratieri.controller;

import com.baratieri.automasterbaratieri.dto.request.MecanicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.MecanicoResponseDTO;
import com.baratieri.automasterbaratieri.services.MecanicoService;
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
@RequestMapping(value = "/mecanicos")
@RequiredArgsConstructor
public class MecanicoController {

    private final MecanicoService mecanicoService;

    @GetMapping("/{id}")
    public ResponseEntity<MecanicoResponseDTO> buscarMecanicosId(@PathVariable Long id) {
         MecanicoResponseDTO responseDTO = mecanicoService.buscarMecanicoPorId(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<MecanicoResponseDTO>> buscarMecanicos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String especialidade,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 10, page = 0, sort = "nome") Pageable pageable){

        Page<MecanicoResponseDTO> responseDTOS =
                mecanicoService.buscarMecanico(nome, especialidade, ativo, pageable);
        return ResponseEntity.ok().body(responseDTOS);
    }



    @PostMapping
    public ResponseEntity<MecanicoResponseDTO> salvar(@RequestBody @Valid MecanicoRequestDTO dto) {
        MecanicoResponseDTO mecanicoDto = mecanicoService.salvarMecanico(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").build().toUri();
        return ResponseEntity.created(uri).body(mecanicoDto);
    }
}
