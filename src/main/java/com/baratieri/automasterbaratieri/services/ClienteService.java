package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.ClienteRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ClienteResponseDTO;
import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.repositories.ClienteRepository;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;
import com.baratieri.automasterbaratieri.services.util.FormatacaoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarClientePorId(Long clienteId) {
        Cliente cliente = validarClienteId(clienteId);
        return ClienteResponseDTO.fromEntity(cliente);
    }


    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarClientes(String nome,
                                                   String cpfOuCnpj,
                                                   Pageable pageable) {
        String cpfOuCnpjLimpo = FormatacaoUtil.limparDocumento(cpfOuCnpj);
        Page<Cliente> clientes = clienteRepository.buscarClentesComFiltros(nome, cpfOuCnpjLimpo, pageable);
        return clientes.map(ClienteResponseDTO::fromEntity);
    }

    @Transactional
    public ClienteResponseDTO salvarCliente(ClienteRequestDTO dto) {

        String cpfOuCnpjLimpo = FormatacaoUtil.limparDocumento(dto.cpfOuCnpj());
        FormatacaoUtil.validarDocumentoUnico(
                cpfOuCnpjLimpo,
                dto.cpfOuCnpj(),
                "Cliente",
                clienteRepository::existsByCpfOuCnpj
        );
        Cliente cliente = new Cliente(
                dto.nome(),
                cpfOuCnpjLimpo,
                dto.endereco(),
                dto.telefone(),
                dto.email()
        );

        return ClienteResponseDTO.fromEntity(clienteRepository.save(cliente));
    }

    private Cliente validarClienteId(Long clienteId) {
        return clienteRepository.findById(clienteId).
                orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrada com ID: " + clienteId));
    }
}

