package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.AtualizarMecanicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.request.MecanicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.MecanicoResponseDTO;
import com.baratieri.automasterbaratieri.entities.Mecanico;
import com.baratieri.automasterbaratieri.repositories.MecanicoRepository;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;
import com.baratieri.automasterbaratieri.services.util.FormatacaoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MecanicoService {
    private final MecanicoRepository mecanicoRepository;

    @Transactional(readOnly = true)
    public MecanicoResponseDTO buscarMecanicoPorId(Long id) {
        Mecanico mecanico = buscarOuFalhar(id);
        return MecanicoResponseDTO.fromEntity(mecanico);
    }

    @Transactional(readOnly = true)
    public Page<MecanicoResponseDTO> buscarMecanico(String nome,
                                                    String especialidade,
                                                    Boolean ativo, Pageable pageable) {
        Page<Mecanico> mecanicoPage =
                mecanicoRepository.buscarMecanicoComFiltro(nome,especialidade,ativo,pageable);

        return mecanicoPage.map(MecanicoResponseDTO::fromEntity);
    }


    @Transactional
    public MecanicoResponseDTO salvarMecanico(MecanicoRequestDTO dto) {

        String cpfLimpo = FormatacaoUtil.limparDocumento(dto.cpf());
        FormatacaoUtil.validarDocumentoUnico(
                cpfLimpo,
                dto.cpf(),
                "Mecânico",
                mecanicoRepository::existsByCpf
        );
        Mecanico mecanico = new Mecanico(dto.nome(), cpfLimpo, dto.especialidade(),
                dto.taxaComissao());
        return MecanicoResponseDTO.fromEntity(mecanicoRepository.save(mecanico));
    }

    @Transactional
    public void excluirMecanico(Long id) {
        Mecanico mecanico = buscarOuFalhar(id);
        mecanico.inativar();
        mecanicoRepository.save(mecanico);
    }

    @Transactional
    public MecanicoResponseDTO atualizarMecanico(Long id, AtualizarMecanicoRequestDTO dto) {
        Mecanico mecanico = buscarOuFalhar(id);
        mecanico.atualizarDados(dto.taxaComissao());
        return MecanicoResponseDTO.fromEntity(mecanicoRepository.save(mecanico));
    }


    public Mecanico buscarOuFalhar(Long id) {
        return mecanicoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Mecânico não encontrada com ID: " + id));

    }

}


