package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.ServicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ServicoResponseDTO;
import com.baratieri.automasterbaratieri.entities.Servico;
import com.baratieri.automasterbaratieri.repositories.ServicoRepository;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MaoDeObraService {

    private final ServicoRepository servicoRepository;

    @Transactional(readOnly = true)
    public ServicoResponseDTO buscarServicoPorId(Long idServico) {
     Servico servico = validarServicoPorId(idServico);
     return ServicoResponseDTO.fromEntity(servico);
    }

    @Transactional(readOnly = true)
    public Page<ServicoResponseDTO> pesquisarServicos(String descricao, Pageable pageable) {

        Page<Servico> paginaDeServicos = servicoRepository.buscarServicosComFiltros(descricao, pageable);

        return paginaDeServicos.map(ServicoResponseDTO::fromEntity);
    }


    @Transactional
    public ServicoResponseDTO salvarMaoDeObra(ServicoRequestDTO dto) {

        if (servicoRepository.existsByDescricaoIgnoreCase(dto.descricao())) {
            throw new RegraNegocioException("Já existe um serviço cadastrado com a descrição: " + dto.descricao());
        }

        Servico servico = new Servico();
        servico.setDescricao(dto.descricao().toUpperCase().trim());
        servico.setValorMaoDeObraBase(dto.valorMaoDeObraBase());

        servico = servicoRepository.save(servico);

        return ServicoResponseDTO.fromEntity(servico);
    }

    private Servico validarServicoPorId(Long idServico) {
        return servicoRepository.findById(idServico).orElseThrow(() ->
                new ResourceNotFoundException("Serviço não encontrada com ID: " + idServico));

    }
}
