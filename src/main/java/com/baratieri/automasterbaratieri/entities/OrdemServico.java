package com.baratieri.automasterbaratieri.entities;

import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.baratieri.automasterbaratieri.services.util.ValidadorUtil.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Veiculo veiculo;

    @Enumerated(EnumType.STRING)
    private StatusOS status;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;

    private String descricao;

    @Column(name = "valor_total")
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemPeca> itensPeca = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemServico> itensServico = new ArrayList<>();


    public OrdemServico(Veiculo veiculo, String descricao) {
        this.veiculo = veiculo;
        this.descricao = descricao;
        this.dataAbertura = LocalDateTime.now();
        this.status = StatusOS.AGUARDANDO_APROVACAO;

    }

    public void adicionarPecaOrdemServico(ItemPeca item) {
        validarStatusOrdemServicoFinalizadaOuCancelada();
        itensPeca.add(item);
        calcularTotal();
    }

    public void adicionarServico(ItemServico item) {
        validarStatusOrdemServicoFinalizadaOuCancelada();
        itensServico.add(item);
        calcularTotal();
    }

    public void removerPecaOrdemServico(ItemPeca item) {
        if (item == null) {
            throw new RegraNegocioException("O item de peça não pode ser nulo.");
        }
        validarStatusOrdemServicoFinalizadaOuCancelada();
        if (!itensPeca.contains(item)) {
            throw new RegraNegocioException("Este item de peça não pertence a esta Ordem de Serviço.");
        }
        item.getPeca().adicionarEstoque(item.getQuantidade());
        itensPeca.remove(item);

        calcularTotal();
    }

    public void removerItemServico(ItemServico item) {
        if (item == null) {
            throw new RegraNegocioException("O item de serviço não pode ser nulo.");
        }
        validarStatusOrdemServicoFinalizadaOuCancelada();

        if (!itensServico.contains(item)) {
            throw new RegraNegocioException("Este item de serviço não pertence a esta Ordem de Serviço.");
        }

        this.itensServico.remove(item);
        this.calcularTotal();
    }

    public void calcularTotal() {
        BigDecimal totalPecas = BigDecimal.ZERO;

        if (itensPeca != null && !itensPeca.isEmpty()) {
            totalPecas = itensPeca.stream()
                    .map(ItemPeca::getSubtotal) // Chama o getSubtotal() de cada item
                    .filter(Objects::nonNull)  // Segurança contra nulos
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma acumulada
        }

        //Soma o total dos SERVIÇOS
        BigDecimal totalServicos = BigDecimal.ZERO;

        if (itensServico != null && !itensServico.isEmpty()) {
            totalServicos = itensServico.stream()
                    .map(ItemServico::getSubtotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        this.valorTotal = totalPecas.add(totalServicos);
    }

    public void atualizarDescricaoServico(String descricao) {
        validarDadosObrigatorio(descricao, "A descrição do serviço é obrigatória.");
        this.descricao = descricao;
    }

    public void cancelarOs() {
        validarStatusOrdemServicoFinalizadaOuCancelada();
        status = StatusOS.CANCELADO;
        dataFechamento = LocalDateTime.now();
    }

    public void estornarPecasAoEstoque() {
        if (itensPeca != null) {
            itensPeca.forEach(item -> item.getPeca().adicionarEstoque(item.getQuantidade()));
        }
    }

    public void aprovarOrcamento() {
        validarStatusOrdemServicoOrcamento(status, "Essa OS não pode ser iniciada pois está: "
                + status);
        status = StatusOS.EM_EXECUCAO;
    }

    public void finalizarOs() {
        validarStatusOrdemServicoEmExecucao(status, "Não é possível finalizar uma O.S. com status " + status +
                ". A O.S. precisa estar EM_EXECUCAO.");

        status = StatusOS.FINALIZADO;
        dataFechamento = LocalDateTime.now();
    }

    public void aguardarOs() {
        status = StatusOS.AGUARDANDO_APROVACAO;
    }

    private void validarStatusOrdemServicoFinalizadaOuCancelada() {
        if (status == StatusOS.FINALIZADO || status == StatusOS.CANCELADO) {
            throw new RegraNegocioException("Não é possível adicionar peças em uma OS encerrada ou cancelada.");
        }
    }

    public void validarPecasOrdemServicoAprovada() {
        validarExistePecaOrdemServico(itensServico, itensPeca,
                "Não é possível aprovar a Ordem de Serviço. " +
                        "Adicione pelo menos uma Peça ou Serviço ao orçamento.");
    }

    @PrePersist
    @PreUpdate
    public void garantirCalculoAntesDeSalvar() {
        this.calcularTotal();
    }

}
