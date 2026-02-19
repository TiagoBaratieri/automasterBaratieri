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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class VeiculoService {
    private VeiculoRepository veiculoRepository;
    private ClienteRepository clienteRepository;

    @Transactional
    public VeiculoResponseDTO salvar(VeiculoRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.idCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + dto.idCliente()));
        Veiculo veiculo = new Veiculo();

        veiculo.setId(veiculo.getId());
        veiculo.setPlaca(dto.placa());
        veiculo.setMarca(dto.marca());
        veiculo.setModelo(dto.modelo());
        veiculo.setAno(dto.ano());
        veiculo.setCliente(cliente);

        veiculoRepository.save(veiculo);
        return VeiculoResponseDTO.fromEntity(veiculo);
    }
}
