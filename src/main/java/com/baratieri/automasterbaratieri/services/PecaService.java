package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.PecaRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PecaResponseDTO;
import com.baratieri.automasterbaratieri.entities.Peca;
import com.baratieri.automasterbaratieri.repositories.PecaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PecaService {
    private PecaRepository pecaRepository;

    @Transactional
    public PecaResponseDTO salvar(PecaRequestDTO dto){
        Peca  peca = new Peca();
        peca.setSku(dto.sku());
        peca.setNome(dto.nome());
        peca.setPartNumber(dto.partNumber());
        peca.setPrecoCusto(dto.precoCusto());
        peca.setPrecoVenda(dto.precoVenda());
        peca.setQuantidadeEstoque(dto.quantidadeEstoque());
        peca.setEstoqueMinimo(dto.estoqueMinimo());

        pecaRepository.save(peca);

        return PecaResponseDTO.fromEntity(peca);
    }
}
