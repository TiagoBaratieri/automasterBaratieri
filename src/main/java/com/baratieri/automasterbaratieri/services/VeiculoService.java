package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.VeiculoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.VeiculoResponseDTO;
import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.entities.Veiculo;
import com.baratieri.automasterbaratieri.repositories.ClienteRepository;
import com.baratieri.automasterbaratieri.repositories.VeiculoRepository;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;
import com.baratieri.automasterbaratieri.services.util.FormatacaoUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public VeiculoResponseDTO buscarVeiculoPorId(Long veiculoId) {
       Veiculo veiculo = validarIdVeiculo(veiculoId);
       return VeiculoResponseDTO.fromEntity(veiculo);
    }

    @Transactional(readOnly = true)
    public VeiculoResponseDTO buscarVeiculo(String placa) {
        String placaLimpa = FormatacaoUtil.limparPlaca(placa);
        Veiculo veiculo = validarExistePlacaVeiculo(placaLimpa);
        return  VeiculoResponseDTO.fromEntity(veiculo);
    }

    @Transactional(readOnly = true)
    public Page<VeiculoResponseDTO> listarVeiculos(Long clienteId,
                                                   String placa,
                                                   Pageable pageable) {
        String placaLimpa = FormatacaoUtil.limparPlaca(placa);
        Page<Veiculo> veiculos = veiculoRepository.buscarVeiculosComFiltros(clienteId, placaLimpa, pageable);
        return veiculos.map(VeiculoResponseDTO::fromEntity);
    }

    @Transactional
    public VeiculoResponseDTO salvarVeiculo(VeiculoRequestDTO dto) {

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

    private Veiculo validarExistePlacaVeiculo(String placa) {
        return veiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com a placa: " + placa));
    }

    private Veiculo validarIdVeiculo(Long veiculoId) {
        return veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo não encontrada com ID: " + veiculoId));
    }
}
