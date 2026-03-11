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

    @Column(name = "orcamento_alterado", nullable = false, columnDefinition = "boolean default false")
    private boolean orcamentoAlterado = false;

    @Column(name = "orcamento_revisado", nullable = false, columnDefinition = "boolean default false")
    private boolean orcamentoRevisado = false;

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
        ordemServicoAguardandoAprovacaoOuExecucao();
        itensPeca.add(item);
        calcularTotal();
    }

    public void adicionarServico(ItemServico item) {
        validarStatusOrdemServicoFinalizadaOuCancelada();
        ordemServicoAguardandoAprovacaoOuExecucao();
        itensServico.add(item);
        calcularTotal();
    }

    public void removerPecaOrdemServico(ItemPeca item) {
        if (item == null) {
            throw new RegraNegocioException("O item de peça não pode ser nulo.");
        }
        validarStatusOrdemServicoFinalizadaOuCancelada();
        ordemServicoAguardandoAprovacaoOuExecucao();
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
        ordemServicoAguardandoAprovacaoOuExecucao();

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
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new RegraNegocioException("A descrição do serviço é obrigatória.");
        }
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
        if (status != StatusOS.AGUARDANDO_APROVACAO) {
            throw new RegraNegocioException("Essa OS não pode ser iniciada pois está: " + this.status);
        }
        status = StatusOS.EM_EXECUCAO;
    }

    public void finalizarOs() {
        if (status != StatusOS.EM_EXECUCAO) {
            throw new RegraNegocioException("Não é possível finalizar uma O.S. com status " + status + ". A O.S. precisa estar EM_EXECUCAO.");
        }
        status = StatusOS.FINALIZADO;
        dataFechamento = LocalDateTime.now();
    }

    public void aguardarOs() {
        status = StatusOS.AGUARDANDO_APROVACAO;

        if (orcamentoAlterado) {
            orcamentoRevisado = true;
        }

        orcamentoAlterado = false;
    }


    public void validarPecasOrdemServicoAprovada() {
        boolean semPecas = itensPeca == null || itensPeca.isEmpty();
        boolean semServicos = itensServico == null || itensServico.isEmpty();

        if (semPecas && semServicos) {
            throw new RegraNegocioException("Não é possível aprovar a Ordem de Serviço. Adicione pelo menos uma Peça ou Serviço ao orçamento.");
        }

    }

    public void validarReenvioOrcamento(String motivo) {
        boolean temMotivo = motivo != null && !motivo.isBlank();
        if (!orcamentoAlterado && temMotivo) {
            throw new RegraNegocioException("Não é possível classificar como 'Atualizado'. " +
                    "Nenhuma peça ou serviço foi alterado nesta O.S. Se deseja apenas reenviar o e-mail original, deixe o motivo em branco.");
        }

        if (orcamentoAlterado && !temMotivo) {
            throw new RegraNegocioException("O orçamento desta O.S. sofreu alterações." +
                    " É obrigatório informar o 'motivo' para enviar ao cliente.");
        }

        if (orcamentoRevisado && !orcamentoAlterado && !temMotivo) {
            throw new RegraNegocioException("Este orçamento já é uma versão atualizada. Para reenviar a 2ª via, é obrigatório preencher o motivo (Ex: 'Reenvio do orçamento com a bomba de água').");
        }
    }

    private void validarStatusOrdemServicoFinalizadaOuCancelada() {
        if (status == StatusOS.FINALIZADO || status == StatusOS.CANCELADO) {
            throw new RegraNegocioException("Não é possível adicionar peças em uma OS encerrada ou cancelada.");
        }
    }

    private void ordemServicoAguardandoAprovacaoOuExecucao() {
        if (status == StatusOS.AGUARDANDO_APROVACAO || status == StatusOS.EM_EXECUCAO) {
            orcamentoAlterado = true;
        }

    }

    @PrePersist
    @PreUpdate
    public void garantirCalculoAntesDeSalvar() {
        this.calcularTotal();
    }

}
