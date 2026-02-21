package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.MecanicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.MecanicoResponseDTO;
import com.baratieri.automasterbaratieri.entities.Mecanico;
import com.baratieri.automasterbaratieri.repositories.MecanicoRepository;
import com.baratieri.automasterbaratieri.services.util.DocumentoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MecanicoService {
    private final MecanicoRepository mecanicoRepository;

    @Transactional
    public MecanicoResponseDTO salvar(MecanicoRequestDTO dto) {

        String cpfLimpo = DocumentoUtil.limparDocumento(dto.cpf());
        DocumentoUtil.validarDocumentoUnico(
                cpfLimpo,
                dto.cpf(),
                "Mecânico",
                mecanicoRepository::existsByCpf
        );
        Mecanico mecanico = new Mecanico(dto.nome(), cpfLimpo, dto.especialidade(),
                dto.taxaComissao(), dto.ativo());
        return MecanicoResponseDTO.fromDTO(mecanicoRepository.save(mecanico));
    }

}


