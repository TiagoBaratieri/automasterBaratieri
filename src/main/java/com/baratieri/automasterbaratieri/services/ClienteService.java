package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.ClienteRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ClienteResponseDTO;
import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.repositories.ClienteRepository;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ClienteService {

    private ClienteRepository clienteRepository;

    @Transactional
    public ClienteResponseDTO salvar(ClienteRequestDTO dto) {
        if (clienteRepository.existsByCpfOuCnpj(dto.cpfOuCnpj())) {
            throw new RegraNegocioException("Jà existe este Cliente cadastrado com esse CPF/CNPJ");
        }
        Cliente cliente = clienteRepository.save(dto.toEntity());

        return ClienteResponseDTO.fromEntity(cliente);
    }
}
