package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.*;
import com.baratieri.automasterbaratieri.dto.response.OrdemServicoResponseDTO;
import com.baratieri.automasterbaratieri.entities.*;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.repositories.*;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final ItemServicoRepository itemServicoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final MecanicoRepository mecanicoRepository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;
    private final ItemPecaRepository itemPecaRepository;
    private final PecaRepository pecaRepository;


    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarOrdemServicoPorId(Long id) {
       OrdemServico os = ordemServicoExiste(id);
        return OrdemServicoResponseDTO.fromEntity(os);
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> buscarTodosOrdemServico(String placa, StatusOS status) {
       return filtrarOrdemServico(status, placa);
    }

    @Transactional
    public OrdemServicoResponseDTO abrirOdemServico(AberturaOsRequestDTO dto) {
        Veiculo veiculo = validarVeiculoExistente(dto);
        validarOrdemServicoAberta(veiculo);

        OrdemServico os = new OrdemServico(veiculo, dto.observacaoInicial());
        return OrdemServicoResponseDTO.fromEntity(ordemServicoRepository.save(os));

    }

    @Transactional
    public OrdemServicoResponseDTO adicionarMaoDeObraOrdemServico(Long osId, ServicoPayloadDTO payloadDTO) {
        OrdemServico os = ordemServicoExiste(osId);
        Servico servico = validarServicoExistente(payloadDTO);
        Mecanico mecanico = validarMecanicoExistente(payloadDTO);

        ItemServico itemServico = new ItemServico(os,servico,mecanico,payloadDTO.valorCobrado(),
                payloadDTO.quantidade(),payloadDTO.observacao());

        os.adicionarItemMaoDeObra(itemServico);
        itemServicoRepository.save(itemServico);

        return OrdemServicoResponseDTO.fromEntity(os);

    }

    @Transactional
    public OrdemServicoResponseDTO adicionarPecaOrdemServico(Long osId, PecaPayloadDTO payload) {
        OrdemServico os = ordemServicoExiste(osId);
        Peca peca = validarPecaExistente(payload);
        peca.baixarEstoque(payload.quantidade());

        ItemPeca itemPeca = new ItemPeca(os,peca, payload.quantidade(), payload.valorUnitario());

        os.adicionarItemOrdemServico(itemPeca);
        itemPecaRepository.save(itemPeca);

        return OrdemServicoResponseDTO.fromEntity(os);

    }

    @Transactional
    public OrdemServicoResponseDTO cancelarOrdemServico(Long id) {
        OrdemServico os = ordemServicoExiste(id);
        os.cancelarOs();
        os.restornarPecasAoEstoque();
        ordemServicoRepository.save(os);

        return OrdemServicoResponseDTO.fromEntity(os);

    }

    @Transactional
    public OrdemServicoResponseDTO aprovarOrdemServico(Long id) {
        OrdemServico os = ordemServicoExiste(id);
        os.aprovarOrcamento();
        os = ordemServicoRepository.save(os);
        return OrdemServicoResponseDTO.fromEntity(os);
    }

    @Transactional
    public OrdemServicoResponseDTO finalizarOrdemServico(Long id) {
        OrdemServico os = ordemServicoExiste(id);
        os.finalizarOs();
        os.calcularTotal();
        return OrdemServicoResponseDTO.fromEntity(ordemServicoRepository.save(os));
    }

    private void validarOrdemServicoAberta(Veiculo veiculo) {
        boolean jaTemOsAberta = ordemServicoRepository.existsByVeiculoAndStatusIn(
                veiculo,
                StatusOS.getAtivos());

        if (jaTemOsAberta) {
            throw new RegraNegocioException("Este veículo já possui uma Ordem de Serviço em andamento.");
        }
    }

    private OrdemServico ordemServicoExiste(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id));
    }

    private List<OrdemServicoResponseDTO> filtrarOrdemServico(StatusOS status, String placa) {
        List<OrdemServico> ordemServicos = ordemServicoRepository.buscarOSComFiltros(placa, status);

        if (ordemServicos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma Ordem de Serviço encontrada para os filtros informados.");
        }
        return ordemServicos.stream()
                .map(OrdemServicoResponseDTO::fromEntity)
                .toList();
    }

    private Veiculo validarVeiculoExistente(AberturaOsRequestDTO dto) {
         return veiculoRepository.findByPlaca(dto.placaVeiculo())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com a placa: " +
                        dto.placaVeiculo()));
    }

    private Servico validarServicoExistente(ServicoPayloadDTO dto) {
        return servicoRepository.findById(dto.servicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado no catálogo com ID: " + dto.servicoId()));

    }

    private Mecanico validarMecanicoExistente(ServicoPayloadDTO dto) {
        return mecanicoRepository.findByIdAndAtivoTrue(dto.mecanicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Mecânico não encontrado ou Inativo. Verifique se o cadastro está ativo."
                ));
    }

    private Peca validarPecaExistente(PecaPayloadDTO dto) {
        return  pecaRepository.findById(dto.pecaId())
                .orElseThrow(() -> new ResourceNotFoundException("Peça não encontrada."));

    }
}
