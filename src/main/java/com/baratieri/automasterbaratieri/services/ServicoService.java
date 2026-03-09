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
public class ServicoService {

    private final ServicoRepository servicoRepository;

    @Transactional(readOnly = true)
    public ServicoResponseDTO buscarServicoPorId(Long id) {
        Servico servico = servicoExiste(id);
        return ServicoResponseDTO.fromEntity(servico);
    }

    @Transactional(readOnly = true)
    public Page<ServicoResponseDTO> buscarServicos(String descricao, Pageable pageable) {

        Page<Servico> paginaDeServicos = servicoRepository.buscarServicosComFiltros(descricao, pageable);

        return paginaDeServicos.map(ServicoResponseDTO::fromEntity);
    }

    @Transactional
    public ServicoResponseDTO salvarServico(ServicoRequestDTO dto) {
        validarDescricaoExiste(dto);
        Servico servico = new Servico();
        servico.preencherDados(dto.descricao(),  dto.valorMaoDeObraBase());
        return ServicoResponseDTO.fromEntity(servicoRepository.save(servico));
    }

    @Transactional
    public ServicoResponseDTO atualizarServico(Long id, ServicoRequestDTO dto) {
        Servico servico = servicoExiste(id);
        validarDescricaoExiste(dto);
        servico.preencherDados(dto.descricao(), dto.valorMaoDeObraBase());
        return ServicoResponseDTO.fromEntity(servico);
    }

    @Transactional
    public void excluirServico(Long id) {
        Servico servico = servicoExiste(id);
        servico.inativar();

        servicoRepository.save(servico);
    }

    public Servico servicoExiste(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado no catálogo com ID: " + id));
    }

    private void validarDescricaoExiste(ServicoRequestDTO dto) {
        if (servicoRepository.existsByDescricaoIgnoreCase(dto.descricao())) {
            throw new RegraNegocioException("Já existe um serviço cadastrado com a descrição: " + dto.descricao());
        }
    }
}
