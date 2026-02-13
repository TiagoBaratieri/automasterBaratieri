package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.MecanicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.MecanicoResponseDTO;
import com.baratieri.automasterbaratieri.entities.Mecanico;
import com.baratieri.automasterbaratieri.repositories.MecanicoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MecanicoService {
    private MecanicoRepository mecanicoRepository;

    @Transactional
    public MecanicoResponseDTO salvar(MecanicoRequestDTO dto) {
        String cpfLimpo = dto.cpf().replaceAll("\\D", "");
        Mecanico mecanico = new Mecanico();
        mecanico.setNome(dto.nome());
        mecanico.setCpf(cpfLimpo);
        mecanico.setEspecialidade(dto.especialidade());
        mecanico.setTaxaComissao(dto.taxaComissao());
        mecanico.setAtivo(dto.ativo());
        mecanicoRepository.save(mecanico);

        return MecanicoResponseDTO.fromDTO(mecanico);
    }
}
