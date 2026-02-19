package com.baratieri.automasterbaratieri.services;

import com.baratieri.automasterbaratieri.dto.request.AberturaOsRequestDTO;
import com.baratieri.automasterbaratieri.dto.request.ItemPecaRequestDTO;
import com.baratieri.automasterbaratieri.dto.request.ItemServicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.OrdemServicoResponseDTO;
import com.baratieri.automasterbaratieri.entities.*;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.repositories.*;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;


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
    public OrdemServicoResponseDTO buscarPorId(Long id) {
        OrdemServico os = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OS não encontrada com ID: " + id));

        return OrdemServicoResponseDTO.fromEntity(os);
    }
    @Transactional
    public OrdemServicoResponseDTO abrirOdemServico(AberturaOsRequestDTO dto) {

        Veiculo veiculo = veiculoRepository.findByPlaca(dto.placaVeiculo())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com a placa: " +
                        dto.placaVeiculo()));

        validarOsAberta(veiculo);

        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setVeiculo(veiculo);
        ordemServico.setDataAbertura(LocalDateTime.now());
        ordemServico.setStatus(StatusOS.ORCAMENTO);
        ordemServico.setDescricao(dto.observacaoInicial());

        return OrdemServicoResponseDTO.fromEntity(ordemServicoRepository.save(ordemServico));

    }

    @Transactional
    public OrdemServicoResponseDTO lancarServico(ItemServicoRequestDTO dto) {
        OrdemServico os = ordemServicoRepository.findById(dto.ordemServicoId())
                .orElseThrow(() -> new ResourceNotFoundException("OS não encontrada: " + dto.ordemServicoId()));

        Servico servico = servicoRepository.findById(dto.servicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado no catálogo com ID: " + dto.servicoId()));

        Mecanico mecanico = mecanicoRepository.findByIdAndAtivoTrue(dto.mecanicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Mecânico não encontrado ou Inativo. Verifique se o cadastro está ativo."
                ));

        ItemServico itemServico = new ItemServico();
        itemServico.setServico(servico);
        itemServico.setOrdemServico(os);
        itemServico.setMecanicoResponsavel(mecanico);
        itemServico.setQuantidade(dto.quantidade());
        itemServico.setObservacao(dto.observacao());
        itemServico.setValorCobrado(dto.valorCobrado());

        itemServicoRepository.save(itemServico);

        os.getItensServico().add(itemServico);

        os.calcularTotal();

        return OrdemServicoResponseDTO.fromEntity(os);

    }

    @Transactional
    public OrdemServicoResponseDTO adicionarPecaOrdemServico(ItemPecaRequestDTO dto) {
        OrdemServico os = ordemServicoRepository.findById(dto.ordemServicoId())
                .orElseThrow(() -> new ResourceNotFoundException("OS não encontrada."));
        Peca peca = pecaRepository.findById(dto.pecaId())
                .orElseThrow(() -> new ResourceNotFoundException("Peça não encontrada."));

        peca.baixarEstoque(dto.quantidade());

        ItemPeca itemPeca = new ItemPeca();
        itemPeca.setOrdemServico(os);
        itemPeca.setPeca(peca);
        itemPeca.setQuantidade(dto.quantidade());

        BigDecimal precoPraticado = (dto.valorUnitarioCobrado() != null)
                ? dto.valorUnitarioCobrado()
                : peca.getPrecoVenda();

        itemPeca.setPrecoUnitario(precoPraticado);

        itemPecaRepository.save(itemPeca);
        pecaRepository.save(peca);

        os.getItensPeca().add(itemPeca);
        os.calcularTotal();
        return OrdemServicoResponseDTO.fromEntity(os);

    }

    @Transactional
    public void aprovarOrcamento(Long id) {
        OrdemServico os = buscarOsPorId(id);

        if (os.getStatus() != StatusOS.ORCAMENTO ) {
            throw new RegraNegocioException("Essa OS não pode ser iniciada pois está: "
                    + os.getStatus());
        }

        os.setStatus(StatusOS.EM_EXECUCAO);

        ordemServicoRepository.save(os);
    }

    @Transactional
    public OrdemServicoResponseDTO finalizarOrdemServico(Long id) {
        OrdemServico os = buscarOsPorId(id);
        validaStatusOSFinalizada(os);
        os.setStatus(StatusOS.FINALIZADO);
        os.setDataFechamento(LocalDateTime.now());
        os.calcularTotal();

        return OrdemServicoResponseDTO.fromEntity(ordemServicoRepository.save(os));
    }


    private void validarOsAberta(Veiculo veiculo) {
        boolean jaTemOsAberta = ordemServicoRepository.existsByVeiculoAndStatusIn(
                veiculo,
                StatusOS.getAtivos());

        if (jaTemOsAberta) {
            throw new RegraNegocioException("Este veículo já possui uma Ordem de Serviço em andamento.");
        }

    }

    private void validaStatusOSFinalizada(OrdemServico os) {
        if (!StatusOS.EM_EXECUCAO.equals(os.getStatus())) {
            throw new RegraNegocioException("Não é possível finalizar uma O.S. com status " + os.getStatus() +
                    ". A O.S. precisa estar em EM_EXECUCAO.");
        }
    }

    private OrdemServico buscarOsPorId(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id));
    }

}
