package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.PecaRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PecaResponseDTO;
import com.baratieri.automasterbaratieri.entities.Peca;
import com.baratieri.automasterbaratieri.repositories.PecaRepository;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PecaService {
    private PecaRepository pecaRepository;

    @Transactional
    public PecaResponseDTO salvar(PecaRequestDTO dto){

        String skuFormatado = dto.sku().trim().toUpperCase();

        if (pecaRepository.existsBySku(skuFormatado)) {
            throw new RegraNegocioException("Já existe uma peça cadastrada com o SKU: " + skuFormatado);
        }

        if (dto.partNumber() != null && pecaRepository.existsByPartNumber(dto.partNumber().trim())) {
            throw new RegraNegocioException("Já existe uma peça cadastrada com este Part Number: " + dto.partNumber());
        }
        Peca  peca = new Peca();
        peca.setSku(dto.sku());
        peca.setNome(dto.nome().trim());
        peca.setPartNumber(dto.partNumber());
        peca.setMarca(dto.marca().trim());
        peca.setAplicacao(dto.aplicacao().trim());
        peca.setPrecoCusto(dto.precoCusto());
        peca.setPrecoVenda(dto.precoVenda());
        peca.setQuantidadeEstoque(dto.quantidadeEstoque());
        peca.setEstoqueMinimo(dto.estoqueMinimo());

        pecaRepository.save(peca);

        return PecaResponseDTO.fromEntity(peca);
    }
}
