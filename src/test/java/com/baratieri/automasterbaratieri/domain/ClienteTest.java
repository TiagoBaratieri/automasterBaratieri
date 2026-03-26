package com.baratieri.automasterbaratieri.domain;

import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.entities.Endereco;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = fabricarClientePadrao();
    }

    // =========================================================================
    // Construtor — validações de domínio
    // =========================================================================

    @Test
    @DisplayName("Deve criar cliente com nome formatado em maiúsculo")
    void deveCriarClienteComNomeEmMaiusculo() {
        // O construtor faz nome.trim().toUpperCase()
        assertEquals("JOÃO DA SILVA", cliente.getNome());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar cliente com nome nulo")
    void deveLancarExcecaoAoCriarClienteComNomeNulo() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Cliente(null, "12345678909", null, null, null));

        assertEquals("O nome do cliente é obrigatório.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar cliente com nome em branco")
    void deveLancarExcecaoAoCriarClienteComNomeEmBranco() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Cliente("   ", "12345678909", null, null, null));

        assertEquals("O nome do cliente é obrigatório.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao criar cliente com CPF/CNPJ nulo")
    void deveLancarExcecaoAoCriarClienteComCpfNulo() {
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class, () ->
                new Cliente("João da Silva", null, null, null, null));

        assertEquals("O CPF ou CNPJ é obrigatório.", excecao.getMessage());
    }

    // =========================================================================
    // atualizarDadosDeContato — formatações aplicadas pela entidade
    // =========================================================================

    @Test
    @DisplayName("Deve converter e-mail para minúsculo ao atualizar contato")
    void deveConverterEmailParaMinusculoAoAtualizar() {
        cliente.atualizarDadosDeContato(null, null, "  JOAO@EMAIL.COM  ");

        // formatarEmail → trim().toLowerCase()
        assertEquals("joao@email.com", cliente.getEmail());
    }

    @Test
    @DisplayName("Deve converter telefone para maiúsculo ao atualizar contato")
    void deveConverterTelefoneParaMaiusculoAoAtualizar() {
        // formatarTextoOpcional → trim().toUpperCase()
        // Para telefones o impacto é limpar espaços extras
        cliente.atualizarDadosDeContato(null, "  (41) 99999-9999  ", null);

        assertEquals("(41) 99999-9999", cliente.getTelefone());
    }

    @Test
    @DisplayName("Deve salvar e-mail como null quando campo for enviado em branco")
    void deveSalvarEmailNullQuandoCampoEstiverEmBranco() {
        // formatarEmail("   ") → trim() → isEmpty() → retorna null
        cliente.atualizarDadosDeContato(null, null, "   ");

        assertNull(cliente.getEmail());
    }

    @Test
    @DisplayName("Deve salvar telefone como null quando campo for enviado em branco")
    void deveSalvarTelefoneNullQuandoCampoEstiverEmBranco() {
        // formatarTextoOpcional("   ") → trim() → isEmpty() → retorna null
        cliente.atualizarDadosDeContato(null, "   ", null);

        assertNull(cliente.getTelefone());
    }

    @Test
    @DisplayName("Deve atualizar endereço ao chamar atualizarDadosDeContato")
    void deveAtualizarEnderecoAoAtualizar() {
        Endereco novoEndereco = new Endereco(
                "Av. Brasil", "500", null, "Centro", "80000-000", "Curitiba", "PR");

        cliente.atualizarDadosDeContato(novoEndereco, null, null);

        assertEquals("Av. Brasil", cliente.getEndereco().getLogradouro());
    }

    // =========================================================================
    // inativar / ativar — herdado de Inativavel
    // =========================================================================

    @Test
    @DisplayName("Deve iniciar ativo por padrão ao criar cliente")
    void deveIniciarAtivoAoCriarCliente() {
        assertTrue(cliente.getAtivo(), "Cliente recém-criado deve ter ativo = true");
    }

    @Test
    @DisplayName("Deve inativar cliente ao chamar inativar()")
    void deveInativarClienteAoInativar() {
        cliente.inativar();

        assertFalse(cliente.getAtivo());
    }

    @Test
    @DisplayName("Deve reativar cliente ao chamar ativar()")
    void deveReativarClienteAoAtivar() {
        cliente.inativar();
        cliente.ativar();

        assertTrue(cliente.getAtivo());
    }

    // =========================================================================
    // Fábrica de dados
    // =========================================================================

    private Cliente fabricarClientePadrao() {
        return new Cliente(
                "João da Silva",
                "12345678909",
                new Endereco("Rua das Flores", "123", null, "Centro", "80000-000", "Curitiba", "PR"),
                "(41) 99999-9999",
                "joao@email.com"
        );
    }
}
