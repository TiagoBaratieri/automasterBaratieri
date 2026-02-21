package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.VeiculoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.VeiculoResponseDTO;
import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.entities.Veiculo;
import com.baratieri.automasterbaratieri.repositories.ClienteRepository;
import com.baratieri.automasterbaratieri.repositories.VeiculoRepository;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public VeiculoResponseDTO salvar(VeiculoRequestDTO dto) {

        Cliente cliente = validarClienteExistente(dto);
        Veiculo veiculo = new Veiculo(dto.placa(), dto.marca(), dto.modelo(),
                dto.ano(), cliente);

        veiculoRepository.save(veiculo);
        return VeiculoResponseDTO.fromEntity(veiculo);
    }

    private Cliente validarClienteExistente(VeiculoRequestDTO dto) {
        return clienteRepository.findById(dto.idCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + dto.idCliente()));
    }
}
