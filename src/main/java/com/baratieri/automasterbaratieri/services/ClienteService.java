package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.ClienteRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ClienteResponseDTO;
import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.repositories.ClienteRepository;
import com.baratieri.automasterbaratieri.services.util.DocumentoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteResponseDTO salvar(ClienteRequestDTO dto) {

        String cpfOuCnpjLimpo = DocumentoUtil.limparDocumento(dto.cpfOuCnpj());
        DocumentoUtil.validarDocumentoUnico(
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
}
