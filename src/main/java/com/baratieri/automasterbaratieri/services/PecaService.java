package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.PecaRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PecaResponseDTO;
import com.baratieri.automasterbaratieri.entities.Peca;
import com.baratieri.automasterbaratieri.repositories.PecaRepository;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PecaService {

    private final PecaRepository pecaRepository;


    @Transactional(readOnly = true)
    public PecaResponseDTO buscarPecaPorId(Long pecaId) {
        Peca peca = validarPecaId(pecaId);
        return PecaResponseDTO.fromEntity(peca);
    }

    @Transactional(readOnly = true)
    public Page<PecaResponseDTO> buscarPecas(String nome,
                                             String marca,
                                             String aplicacao, Pageable pageable) {
        Page<Peca> pecaPage = pecaRepository.pesquisarPecaEstoque(nome,marca,aplicacao,pageable);
        return pecaPage.map(PecaResponseDTO::fromEntity);
    }

    @Transactional
    public PecaResponseDTO salvarPecas(PecaRequestDTO dto) {

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

    private Peca validarPecaId(Long pecaId){
        return pecaRepository.findById(pecaId).orElseThrow(() ->
                new ResourceNotFoundException("Peça não encontrada com ID: " + pecaId));

    }
}

