package com.baratieri.automasterbaratieri.dto.response;

import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrdemServicoResponseDTO(
        Long id,
        String protocolo,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento,
        StatusOS status,
        DadosVeiculoDTO veiculo,
        String nomeCliente,
        String descricao,
        List<ItemPecaDTO> pecas,
        List<ItemServicoDTO> servicos,

        BigDecimal valorTotal
) {

    public record DadosVeiculoDTO(String placa, String modelo, String marca) {
    }

    public record ItemServicoDTO(
            String descricaoServico,
            String observacaoMecanico,
            Integer quantidade,
            BigDecimal valorCobrado,
            BigDecimal subtotal
    ) {
    }

    public record ItemPecaDTO(
            String nomePeca,
            String sku,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal subtotal
    ) {
    }

    public static OrdemServicoResponseDTO fromEntity(OrdemServico os) {
        if (os == null) return null;

        // 1. Converte a lista de Peças
        List<ItemPecaDTO> pecasDTO = os.getItensPeca().stream()
                .map(item -> new ItemPecaDTO(
                        item.getPeca().getNome(),
                        item.getPeca().getSku(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getSubtotal()
                )).toList();

        // Converte a lista de Serviços
        List<ItemServicoDTO> servicosDTO = os.getItensServico().stream()
                .map(item -> new ItemServicoDTO(
                        item.getServico().getDescricao(),
                        item.getObservacao(),
                        item.getQuantidade(),
                        item.getValorCobrado(),
                        item.getSubtotal()
                )).toList();

        // Monta o objeto principal
        return new OrdemServicoResponseDTO(
                os.getId(),
                "OS-" + os.getId(),
                os.getDataAbertura(),
                os.getDataFechamento(),
                os.getStatus(),
                new DadosVeiculoDTO(
                        os.getVeiculo().getPlaca(),
                        os.getVeiculo().getModelo(),
                        os.getVeiculo().getMarca()
                ),
                os.getVeiculo().getCliente().getNome(),
                os.getDescricao(),
                pecasDTO,
                servicosDTO,
                os.getValorTotal()
        );
    }
}
