package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.PecaRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PecaResponseDTO;
import com.baratieri.automasterbaratieri.entities.Peca;
import com.baratieri.automasterbaratieri.repositories.PecaRepository;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class PecaService {

    private final PecaRepository pecaRepository;

    @Transactional
    public PecaResponseDTO salvar(PecaRequestDTO dto) {

        String skuLimpo = dto.sku().trim().toUpperCase();
        String partNumberLimpo = dto.partNumber() != null ? dto.partNumber().trim() : null;

        validarSkuPeca(skuLimpo);
        validarPartNumberPeca(partNumberLimpo);

        Peca peca = new Peca(
                skuLimpo,
                dto.nome().trim(),
                partNumberLimpo,
                dto.marca().trim(),
                dto.aplicacao().trim(),
                dto.precoCusto(),
                dto.precoVenda(),
                dto.quantidadeEstoque(),
                dto.estoqueMinimo()
        );

        peca = pecaRepository.save(peca);

        return PecaResponseDTO.fromEntity(peca);
    }

    private void validarSkuPeca(String skuLimpo) {
        if (pecaRepository.existsBySku(skuLimpo)) {
            throw new RegraNegocioException("Já existe uma peça cadastrada com o SKU: " + skuLimpo);
        }
    }

    private void validarPartNumberPeca(String partNumberLimpo) {
        if (partNumberLimpo != null && pecaRepository.existsByPartNumber(partNumberLimpo)) {
            throw new RegraNegocioException("Já existe uma peça cadastrada com este Part Number: " + partNumberLimpo);
        }
    }
}

