package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.*;
import com.baratieri.automasterbaratieri.dto.response.OrdemServicoResponseDTO;
import com.baratieri.automasterbaratieri.entities.*;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.eventos.EstoqueBaixoEvento;
import com.baratieri.automasterbaratieri.eventos.OrcamentoProntoEvento;
import com.baratieri.automasterbaratieri.eventos.OrdemServicoAprovadaEvento;
import com.baratieri.automasterbaratieri.repositories.*;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;

import com.baratieri.automasterbaratieri.services.util.FormatacaoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final ItemServicoRepository itemServicoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final ItemPecaRepository itemPecaRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ServicoService servicoService;
    private final VeiculoService veiculoService;
    private final PecaService pecaService;
    private final MecanicoService mecanicoService;


    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarOrdemServicoPorId(Long id) {
        OrdemServico os = ordemServicoExiste(id);
        return OrdemServicoResponseDTO.fromEntity(os);
    }

    @Transactional(readOnly = true)
    public Page<OrdemServicoResponseDTO> buscarOrdemServico(String placa,
                                                            StatusOS status,
                                                            Pageable pageable) {

        String placaLimpa = FormatacaoUtil.limparPlaca(placa);

        Page<OrdemServico> paginaOs =
                ordemServicoRepository.buscarOrdemServicoComFiltros(placaLimpa, status, pageable);

        return paginaOs.map(OrdemServicoResponseDTO::fromEntity);
    }
    

    @Transactional
    public OrdemServicoResponseDTO abrirOdemServico(AberturaOsRequestDTO dto) {
        Veiculo veiculo = veiculoService.validarExistePlacaVeiculo(dto.placaVeiculo());
        validarOrdemServicoAberta(veiculo);

        OrdemServico os = new OrdemServico(veiculo, dto.observacaoInicial());
        return OrdemServicoResponseDTO.fromEntity(ordemServicoRepository.save(os));
    }

    @Transactional
    public OrdemServicoResponseDTO adicionarServicoOrdemServico(Long osId, ServicoPayloadDTO dto) {
        OrdemServico os = ordemServicoExiste(osId);
        Servico servico = servicoService.servicoExiste(dto.servicoId());
        Mecanico mecanico = mecanicoService.buscarOuFalhar(dto.mecanicoId());

        ItemServico itemServico = new ItemServico(os, servico, mecanico, dto.valorCobrado(),
                dto.quantidade(), dto.observacao());

        os.adicionarServico(itemServico);
        itemServicoRepository.save(itemServico);

        return OrdemServicoResponseDTO.fromEntity(os);

    }

    @Transactional
    public OrdemServicoResponseDTO adicionarPecaOrdemServico(Long osId, PecaPayloadDTO dto) {
        OrdemServico os = ordemServicoExiste(osId);
        Peca peca = pecaService.pecaExiste(dto.pecaId());
        peca.baixarEstoque(dto.quantidade());
        verificarEDispararEventoEstoque(peca);
        ItemPeca itemPeca = new ItemPeca(os, peca, dto.quantidade(), dto.valorUnitario());

        os.adicionarPecaOrdemServico(itemPeca);
        itemPecaRepository.save(itemPeca);

        return OrdemServicoResponseDTO.fromEntity(os);

    }

    @Transactional
    public OrdemServicoResponseDTO atualizarDescricaoServico(Long osId,
                                                             AtualizarObservacaoOsRequestDTO dto) {
        OrdemServico os = ordemServicoExiste(osId);
        os.atualizarDescricaoServico(dto.observacao());
        return OrdemServicoResponseDTO.fromEntity(ordemServicoRepository.save(os));
    }

    @Transactional
    public OrdemServicoResponseDTO removerPecaOrdemServico(Long osId, Long itemPecaId) {
        OrdemServico os = ordemServicoExiste(osId);

        ItemPeca itemPeca = validarItemPecaExistente(itemPecaId);
        os.removerPecaOrdemServico(itemPeca);

        itemPecaRepository.delete(itemPeca);
        return OrdemServicoResponseDTO.fromEntity(ordemServicoRepository.save(os));
    }

    @Transactional
    public OrdemServicoResponseDTO removerServicoOrdemServico(Long osId, Long itemServicoId) {
        OrdemServico os = ordemServicoExiste(osId);

        ItemServico itemServico = validarItemServicoExistente(itemServicoId);
        os.removerItemServico(itemServico);
        itemServicoRepository.delete(itemServico);
        return OrdemServicoResponseDTO.fromEntity(ordemServicoRepository.save(os));
    }

    @Transactional
    public OrdemServicoResponseDTO cancelarOrdemServico(Long id) {
        OrdemServico os = ordemServicoExiste(id);
        os.cancelarOs();
        os.estornarPecasAoEstoque();
        ordemServicoRepository.save(os);

        return OrdemServicoResponseDTO.fromEntity(os);

    }

    @Transactional
    public OrdemServicoResponseDTO aprovarOrdemServico(Long id) {
        OrdemServico os = ordemServicoExiste(id);
        os.aprovarOrcamento();
        os.validarPecasOrdemServicoAprovada();
        os = ordemServicoRepository.save(os);
        eventPublisher.publishEvent(new OrdemServicoAprovadaEvento(os));
        return OrdemServicoResponseDTO.fromEntity(os);
    }

    @Transactional
    public void enviarOrcamento(Long osId,String motivo) {
        OrdemServico os = ordemServicoExiste(osId);
        os.validarPecasOrdemServicoAprovada();
        os.validarReenvioOrcamento(motivo);
        os.aguardarOs();
        ordemServicoRepository.save(os);
        eventPublisher.publishEvent(new OrcamentoProntoEvento(os, motivo));
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

    public OrdemServico ordemServicoExiste(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id));
    }


    private ItemPeca validarItemPecaExistente(Long id) {
        return itemPecaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de peça não encontrado."));

    }

    private ItemServico validarItemServicoExistente(Long id) {
        return itemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de serviço não encontrado."));

    }

    private void verificarEDispararEventoEstoque(Peca peca) {
        if (peca.precisaReporEstoque()){
            eventPublisher.publishEvent(EstoqueBaixoEvento.fromEntity(peca));
        }
    }
}
