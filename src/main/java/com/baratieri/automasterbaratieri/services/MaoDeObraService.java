package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.ServicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ServicoResponseDTO;
import com.baratieri.automasterbaratieri.entities.Servico;
import com.baratieri.automasterbaratieri.repositories.ServicoRepository;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class MaoDeObraService {

    private ServicoRepository servicoRepository;
    @Transactional
    public ServicoResponseDTO salvar(ServicoRequestDTO dto) {

        if (servicoRepository.existsByDescricaoIgnoreCase(dto.descricao())) {
            throw new RegraNegocioException("Já existe um serviço cadastrado com a descrição: " + dto.descricao());
        }

        Servico servico = new Servico();
        servico.setDescricao(dto.descricao().toUpperCase().trim());
        servico.setValorMaoDeObraBase(dto.valorMaoDeObraBase());

        servico = servicoRepository.save(servico);

        return ServicoResponseDTO.fromEntity(servico);
    }
}
