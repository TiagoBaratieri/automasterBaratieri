package com.baratieri.automasterbaratieri.service;

import com.baratieri.automasterbaratieri.dto.request.ClienteAtualizacaoRequestDTO;
import com.baratieri.automasterbaratieri.dto.request.ClienteRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ClienteResponseDTO;
import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.entities.Endereco;
import com.baratieri.automasterbaratieri.repositories.ClienteRepository;
import com.baratieri.automasterbaratieri.services.ClienteService;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    // =========================================================================
    // salvarCliente
    // =========================================================================

    @Test
    @DisplayName("Deve salvar cliente com sucesso, limpando máscara do CPF e formatando nome em maiúsculo")
    void deveSalvarClienteComSucessoQuandoDadosForemValidos() {
        // ARRANGE
        // CPF com máscara → FormatacaoUtil.limparDocumento remove pontuação → "12345678909"
        ClienteRequestDTO dto = fabricarClienteRequestDTO("123.456.789-09");

        when(clienteRepository.existsByCpfOuCnpj("12345678909")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        // ACT
        ClienteResponseDTO responseDTO = clienteService.salvarCliente(dto);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id());
        // Construtor da entidade faz nome.trim().toUpperCase()
        assertEquals("JOÃO DA SILVA", responseDTO.nome());
        // CPF deve estar limpo (sem máscara) conforme persistido
        assertEquals("12345678909", responseDTO.cpfOuCnpj());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve formatar e-mail para minúsculo e sem espaços ao salvar cliente")
    void deveFormatarEmailParaMinusculoAoSalvarCliente() {
        // ARRANGE
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "Maria Souza",
                "98765432100",
                fabricarEndereco(),
                "(11) 98888-7777",
                "  MARIA@EMAIL.COM  " // e-mail com espaços e maiúsculas
        );
        when(clienteRepository.existsByCpfOuCnpj("98765432100")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        ClienteResponseDTO responseDTO = clienteService.salvarCliente(dto);

        // ASSERT
        // FormatacaoUtil.formatarEmail → trim().toLowerCase()
        assertEquals("maria@email.com", responseDTO.email());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException e não salvar quando CPF/CNPJ já estiver cadastrado")
    void deveLancarExcecaoAoSalvarClienteComDocumentoDuplicado() {
        // ARRANGE
        // O CPF com máscara é o que o usuário informou — deve aparecer na mensagem de erro
        ClienteRequestDTO dto = fabricarClienteRequestDTO("123.456.789-09");
        when(clienteRepository.existsByCpfOuCnpj("12345678909")).thenReturn(true);

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> clienteService.salvarCliente(dto));

        // A mensagem deve conter o documento ORIGINAL (com máscara) para orientar o usuário
        assertTrue(excecao.getMessage().contains("123.456.789-09"),
                "Mensagem deveria conter o documento original, mas foi: " + excecao.getMessage());
        assertTrue(excecao.getMessage().contains("Cliente"));
        // Garante que nenhuma gravação ocorreu após a falha de validação
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando o nome do cliente for nulo")
    void deveLancarExcecaoAoSalvarClienteSemNome() {
        // ARRANGE
        ClienteRequestDTO dto = new ClienteRequestDTO(
                null, // nome nulo → construtor da entidade lança RegraNegocioException
                "12345678909",
                fabricarEndereco(),
                "(41) 99999-9999",
                "contato@email.com"
        );
        // A validação do documento passa; a exceção vem do construtor da entidade
        when(clienteRepository.existsByCpfOuCnpj(anyString())).thenReturn(false);

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> clienteService.salvarCliente(dto));

        assertEquals("O nome do cliente é obrigatório.", excecao.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    // =========================================================================
    // buscarClientePorId
    // =========================================================================

    @Test
    @DisplayName("Deve retornar ClienteResponseDTO correto ao buscar cliente por ID existente")
    void deveBuscarClientePorIdComSucesso() {
        // ARRANGE
        Long clienteId = 1L;
        Cliente clienteFake = fabricarClienteEntidade(clienteId);
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteFake));

        // ACT
        ClienteResponseDTO responseDTO = clienteService.buscarClientePorId(clienteId);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals(clienteId, responseDTO.id());
        assertEquals("JOÃO DA SILVA", responseDTO.nome());
        assertEquals("12345678909", responseDTO.cpfOuCnpj());
        verify(clienteRepository, times(1)).findById(clienteId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException com mensagem exata quando ID do cliente não existir")
    void deveLancarResourceNotFoundExceptionQuandoClienteNaoExistir() {
        // ARRANGE
        Long idInexistente = 999L;
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> clienteService.buscarClientePorId(idInexistente));

        assertEquals("Cliente não encontrada com ID: 999", excecao.getMessage());
    }

    // =========================================================================
    // listarClientes
    // =========================================================================

    @Test
    @DisplayName("Deve limpar máscara do CPF antes de filtrar e retornar Page<ClienteResponseDTO>")
    void deveLimparMascaraDoCpfERetornarPaginacaoAoListarClientes() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Cliente clienteFake = fabricarClienteEntidade(1L);
        Page<Cliente> paginaFake = new PageImpl<>(List.of(clienteFake), pageable, 1);

        // CPF com máscara → limparDocumento → "12345678909" é o que chega ao repositório
        when(clienteRepository.buscarClentesComFiltros("joão", "12345678909", pageable))
                .thenReturn(paginaFake);

        // ACT — cpfOuCnpj é passado com máscara pelo chamador
        Page<ClienteResponseDTO> resultado = clienteService.listarClientes("joão", "123.456.789-09", pageable);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("JOÃO DA SILVA", resultado.getContent().get(0).nome());
        // Garante que o repositório recebeu o CPF sem máscara
        verify(clienteRepository).buscarClentesComFiltros("joão", "12345678909", pageable);
    }

    @Test
    @DisplayName("Deve passar null ao repositório quando CPF/CNPJ não for informado no filtro")
    void devePassarNullAoRepositorioQuandoCpfNaoForInformadoNoFiltro() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> paginaVazia = new PageImpl<>(List.of(), pageable, 0);
        // FormatacaoUtil.limparDocumento(null) → retorna null sem lançar NullPointerException
        when(clienteRepository.buscarClentesComFiltros(null, null, pageable))
                .thenReturn(paginaVazia);

        // ACT
        Page<ClienteResponseDTO> resultado = clienteService.listarClientes(null, null, pageable);

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(clienteRepository).buscarClentesComFiltros(null, null, pageable);
    }

    // =========================================================================
    // atualizarCliente
    // =========================================================================

    @Test
    @DisplayName("Deve atualizar dados de contato do cliente sem chamar save() (JPA dirty-checking)")
    void deveAtualizarContatoDoClienteComSucesso() {
        // ARRANGE
        Long clienteId = 1L;
        Cliente clienteFake = fabricarClienteEntidade(clienteId);
        Endereco novoEndereco = new Endereco(
                "Rua Nova", "200", null, "Novo Bairro", "80000-000", "Curitiba", "PR");
        ClienteAtualizacaoRequestDTO dto = new ClienteAtualizacaoRequestDTO(
                novoEndereco,
                "(41) 88888-8888",
                "NOVO@EMAIL.COM"  // deve ser convertido para minúsculo por formatarEmail
        );
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteFake));

        // ACT
        ClienteResponseDTO responseDTO = clienteService.atualizarCliente(clienteId, dto);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals("novo@email.com", responseDTO.email());
        // CRÍTICO: atualizarCliente depende de @Transactional (dirty-checking do JPA),
        // portanto save() NÃO deve ser chamado diretamente
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar cliente com ID inexistente")
    void deveLancarExcecaoAoAtualizarClienteComIdInexistente() {
        // ARRANGE
        Long idInexistente = 999L;
        ClienteAtualizacaoRequestDTO dto = new ClienteAtualizacaoRequestDTO(
                null, "(41) 00000-0000", "email@email.com");
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> clienteService.atualizarCliente(idInexistente, dto));

        assertEquals("Cliente não encontrada com ID: 999", excecao.getMessage());
    }

    // =========================================================================
    // excluirCliente
    // =========================================================================

    @Test
    @DisplayName("Deve inativar logicamente o cliente (ativo = false) e chamar save() exatamente 1 vez")
    void deveInativarClienteLogicamenteComSucessoAoExcluir() {
        // ARRANGE
        Long clienteId = 1L;
        Cliente clienteFake = fabricarClienteEntidade(clienteId);
        assertTrue(clienteFake.getAtivo(), "Pré-condição: cliente deve iniciar como ativo");
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteFake));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        clienteService.excluirCliente(clienteId);

        // ASSERT
        assertFalse(clienteFake.getAtivo(),
                "O cliente deve estar inativo (ativo = false) após a exclusão lógica");
        verify(clienteRepository, times(1)).save(clienteFake);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException e não tocar no banco ao excluir ID inexistente")
    void deveLancarExcecaoAoExcluirClienteComIdInexistente() {
        // ARRANGE
        Long idInexistente = 999L;
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> clienteService.excluirCliente(idInexistente));

        assertEquals("Cliente não encontrada com ID: 999", excecao.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    // =========================================================================
    // Fábricas de Dados (Test Fixtures)
    // =========================================================================

    /**
     * DTO de requisição padrão. O parâmetro cpfOuCnpj aceita valores com ou
     * sem máscara para testar o comportamento de limpeza do FormatacaoUtil.
     */
    private ClienteRequestDTO fabricarClienteRequestDTO(String cpfOuCnpj) {
        return new ClienteRequestDTO(
                "João da Silva",
                cpfOuCnpj,
                fabricarEndereco(),
                "(41) 99999-9999",
                "joao@email.com"
        );
    }

    /**
     * Entidade construída via construtor de domínio (validações aplicadas).
     * Valores pós-construção esperados:
     * - nome       → "JOÃO DA SILVA"  (toUpperCase)
     * - email      → "joao@email.com" (toLowerCase via formatarEmail)
     * - cpfOuCnpj  → "12345678909"   (já limpo, passado sem máscara)
     */
    private Cliente fabricarClienteEntidade(Long id) {
        Cliente cliente = new Cliente(
                "João da Silva",
                "12345678909",
                fabricarEndereco(),
                "(41) 99999-9999",
                "joao@email.com"
        );
        cliente.setId(id);
        return cliente;
    }

    private Endereco fabricarEndereco() {
        return new Endereco(
                "Rua das Flores", "123", "Apto 1",
                "Centro", "80000-000", "Curitiba", "PR"
        );
    }
}
