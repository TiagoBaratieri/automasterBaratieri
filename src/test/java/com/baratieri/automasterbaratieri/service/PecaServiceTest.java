package com.baratieri.automasterbaratieri.service;

import com.baratieri.automasterbaratieri.dto.request.AtualizarPecaRequestDTO;
import com.baratieri.automasterbaratieri.dto.request.PecaRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.PecaResponseDTO;
import com.baratieri.automasterbaratieri.entities.Peca;
import com.baratieri.automasterbaratieri.repositories.PecaRepository;
import com.baratieri.automasterbaratieri.services.PecaService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PecaServiceTest {

    @InjectMocks
    private PecaService pecaService;

    @Mock
    private PecaRepository pecaRepository;

    // =========================================================================
    // salvarPeca
    // =========================================================================

    @Test
    @DisplayName("Deve salvar peça com sucesso quando todos os dados forem válidos")
    void deveSalvarPecaComSucessoQuandoDadosForemValidos() {
        // ARRANGE
        PecaRequestDTO requestDTO = fabricarPecaRequestDTO();
        when(pecaRepository.existsBySku("SKU123")).thenReturn(false);
        when(pecaRepository.existsByPartNumber("PN123")).thenReturn(false);
        when(pecaRepository.save(any(Peca.class))).thenAnswer(invocation -> {
            Peca p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        // ACT
        PecaResponseDTO responseDTO = pecaService.salvarPeca(requestDTO);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals("SKU123", responseDTO.sku());
        // FormatacaoUtil.formatarTextoOpcional converte para MAIÚSCULO
        assertEquals("PEÇA DE TESTE", responseDTO.nome());
        assertEquals(new BigDecimal("150.00"), responseDTO.precoVenda());
        verify(pecaRepository, times(1)).save(any(Peca.class));
    }

    @Test
    @DisplayName("Deve formatar SKU para maiúsculo e sem espaços antes de salvar")
    void deveFormatarSkuParaMaiusculoAoSalvarPeca() {
        // ARRANGE
        PecaRequestDTO requestDTO = new PecaRequestDTO(
                "  sku-abc  ", // SKU com espaços e letras minúsculas
                "Filtro de Ar",
                "PN-456",
                "Mann",
                "HB20 1.0",
                new BigDecimal("80.00"),
                new BigDecimal("40.00"),
                5,
                2
        );
        // Após formatarTextoOpcional("  sku-abc  ") o SKU esperado é "SKU-ABC"
        when(pecaRepository.existsBySku("SKU-ABC")).thenReturn(false);
        when(pecaRepository.existsByPartNumber(anyString())).thenReturn(false);
        when(pecaRepository.save(any(Peca.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        PecaResponseDTO responseDTO = pecaService.salvarPeca(requestDTO);

        // ASSERT
        assertEquals("SKU-ABC", responseDTO.sku());
        // Garante que a busca no banco foi feita com o valor já formatado
        verify(pecaRepository).existsBySku("SKU-ABC");
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException e não salvar quando o SKU já estiver cadastrado")
    void deveLancarExcecaoAoSalvarQuandoSkuJaExistirNoBanco() {
        // ARRANGE
        PecaRequestDTO requestDTO = fabricarPecaRequestDTO();
        when(pecaRepository.existsBySku("SKU123")).thenReturn(true);

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> pecaService.salvarPeca(requestDTO));

        assertEquals("Já existe uma peça cadastrada com o SKU: SKU123", excecao.getMessage());
        verify(pecaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException e não salvar quando o Part Number já estiver cadastrado")
    void deveLancarExcecaoAoSalvarQuandoPartNumberJaExistirNoBanco() {
        // ARRANGE
        PecaRequestDTO requestDTO = fabricarPecaRequestDTO();
        when(pecaRepository.existsBySku(anyString())).thenReturn(false);
        when(pecaRepository.existsByPartNumber("PN123")).thenReturn(true);

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> pecaService.salvarPeca(requestDTO));

        assertEquals("Já existe uma peça cadastrada com este Part Number: PN123", excecao.getMessage());
        verify(pecaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve salvar peça sem Part Number quando o campo vier em branco (campo opcional)")
    void deveSalvarPecaSemPartNumberQuandoCampoEstiverEmBranco() {
        // ARRANGE
        // Part Number em branco → FormatacaoUtil retorna null → validarPartNumberPeca não aciona
        PecaRequestDTO requestDTO = new PecaRequestDTO(
                "SKU999",
                "Vela de Ignição",
                "   ", // campo opcional em branco
                "NGK",
                "Gol 1.6",
                new BigDecimal("25.00"),
                new BigDecimal("10.00"),
                30,
                5
        );
        when(pecaRepository.existsBySku(anyString())).thenReturn(false);
        when(pecaRepository.save(any(Peca.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        PecaResponseDTO responseDTO = pecaService.salvarPeca(requestDTO);

        // ASSERT
        assertNotNull(responseDTO);
        assertNull(responseDTO.partNumber(), "Part Number deve ser null quando enviado em branco");
        // Regra: se partNumber for null, existsByPartNumber nunca deve ser consultado
        verify(pecaRepository, never()).existsByPartNumber(anyString());
        verify(pecaRepository, times(1)).save(any(Peca.class));
    }

    // =========================================================================
    // buscarPecaPorId
    // =========================================================================

    @Test
    @DisplayName("Deve retornar PecaResponseDTO correto ao buscar peça por ID existente")
    void deveBuscarPecaPorIdComSucesso() {
        // ARRANGE
        Long pecaId = 1L;
        Peca peca = fabricarPecaEntidade(pecaId);
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.of(peca));

        // ACT
        PecaResponseDTO responseDTO = pecaService.buscarPecaPorId(pecaId);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals("SKU123", responseDTO.sku());
        // FormatacaoUtil converte para MAIÚSCULO no construtor da entidade
        assertEquals("FILTRO DE ÓLEO", responseDTO.nome());
        verify(pecaRepository, times(1)).findById(pecaId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException com mensagem correta ao buscar ID inexistente")
    void deveLancarResourceNotFoundExceptionQuandoPecaNaoExistir() {
        // ARRANGE
        Long idInexistente = 999L;
        when(pecaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> pecaService.buscarPecaPorId(idInexistente));

        assertEquals("Peça não encontrada com ID: 999", excecao.getMessage());
    }

    // =========================================================================
    // buscarPecas
    // =========================================================================

    @Test
    @DisplayName("Deve retornar Page<PecaResponseDTO> com os filtros aplicados corretamente")
    void deveBuscarPecasComFiltrosERetornarPaginacaoMapeada() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Peca peca = fabricarPecaEntidade(1L);
        Page<Peca> paginaFake = new PageImpl<>(List.of(peca), pageable, 1);
        when(pecaRepository.pesquisarPecaEstoque("filtro", "Bosch", null, pageable))
                .thenReturn(paginaFake);

        // ACT
        Page<PecaResponseDTO> resultado = pecaService.buscarPecas("filtro", "Bosch", null, pageable);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("SKU123", resultado.getContent().get(0).sku());
        verify(pecaRepository, times(1))
                .pesquisarPecaEstoque("filtro", "Bosch", null, pageable);
    }

    // =========================================================================
    // atualizarPeca
    // =========================================================================

    @Test
    @DisplayName("Deve atualizar preços da peça com sucesso — sem chamar save() (JPA dirty-checking)")
    void deveAtualizarPecaComSucessoQuandoDadosForemValidos() {
        // ARRANGE
        Long pecaId = 1L;
        Peca peca = fabricarPecaEntidade(pecaId);
        AtualizarPecaRequestDTO dto = new AtualizarPecaRequestDTO(
                new BigDecimal("200.00"),
                new BigDecimal("120.00"),
                8
        );
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.of(peca));

        // ACT
        PecaResponseDTO responseDTO = pecaService.atualizarPeca(pecaId, dto);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals(new BigDecimal("200.00"), responseDTO.precoVenda());
        assertEquals(new BigDecimal("120.00"), responseDTO.precoCusto());
        // Nota: Peca.atualizarPeca() atualiza this.quantidadeEstoque com o parâmetro estoqueMinimo
        assertEquals(8, responseDTO.quantidadeEstoque());
        // atualizarPeca no service depende de @Transactional (dirty-checking), portanto NÃO chama save()
        verify(pecaRepository, never()).save(any(Peca.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar peça com ID inexistente")
    void deveLancarExcecaoAoAtualizarPecaComIdInexistente() {
        // ARRANGE
        Long idInexistente = 999L;
        AtualizarPecaRequestDTO dto = new AtualizarPecaRequestDTO(
                new BigDecimal("200.00"), new BigDecimal("100.00"), 5);
        when(pecaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> pecaService.atualizarPeca(idInexistente, dto));

        assertEquals("Peça não encontrada com ID: 999", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar atualizar peça com preço de venda zero ou nulo")
    void deveLancarExcecaoAoAtualizarPecaComPrecoVendaInvalido() {
        // ARRANGE
        Long pecaId = 1L;
        Peca peca = fabricarPecaEntidade(pecaId);
        AtualizarPecaRequestDTO dtoInvalido = new AtualizarPecaRequestDTO(
                BigDecimal.ZERO,           // precoVenda = 0 → viola ValidacaoDominio.validarValorMaiorQueZero
                new BigDecimal("100.00"),
                5
        );
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.of(peca));

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> pecaService.atualizarPeca(pecaId, dtoInvalido));

        assertEquals("O preço de venda deve ser maior que zero.", excecao.getMessage());
    }

    // =========================================================================
    // ExcluirPeca (inativação lógica)
    // =========================================================================

    @Test
    @DisplayName("Deve inativar logicamente a peça (ativo = false) e chamar save() exatamente 1 vez")
    void deveInativarPecaLogicamenteComSucessoAoExcluir() {
        // ARRANGE
        Long pecaId = 1L;
        Peca peca = fabricarPecaEntidade(pecaId);
        assertTrue(peca.getAtivo(), "Pré-condição: peça deve começar ativa");
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.of(peca));
        when(pecaRepository.save(any(Peca.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        pecaService.ExcluirPeca(pecaId);

        // ASSERT
        assertFalse(peca.getAtivo(), "A peça deve estar inativa (ativo = false) após a exclusão lógica");
        verify(pecaRepository, times(1)).save(peca);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException e não tocar no banco ao excluir ID inexistente")
    void deveLancarExcecaoAoExcluirPecaComIdInexistente() {
        // ARRANGE
        Long idInexistente = 999L;
        when(pecaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> pecaService.ExcluirPeca(idInexistente));

        assertEquals("Peça não encontrada com ID: 999", excecao.getMessage());
        verify(pecaRepository, never()).save(any());
    }

    // =========================================================================
    // Fábricas de Dados (Test Fixtures)
    // =========================================================================

    /**
     * DTO de requisição padrão com todos os campos válidos.
     * SKU "SKU123" e PartNumber "PN123" já no formato esperado após formatação.
     */
    private PecaRequestDTO fabricarPecaRequestDTO() {
        return new PecaRequestDTO(
                "SKU123",
                "Peça de Teste",
                "PN123",
                "Bosch",
                "Gol 1.0",
                new BigDecimal("150.00"),
                new BigDecimal("100.00"),
                20,
                10
        );
    }

    /**
     * Entidade Peca construída via construtor de domínio (validações aplicadas).
     * Valores pós-construção: nome="FILTRO DE ÓLEO", sku="SKU123", quantidadeEstoque=10, estoqueMinimo=5.
     */
    private Peca fabricarPecaEntidade(Long id) {
        Peca peca = new Peca(
                "SKU123",
                "Filtro de Óleo",
                "PN-999",
                "Bosch",
                "Gol 1.0",
                new BigDecimal("150.00"),
                new BigDecimal("100.00"),
                10,
                5
        );
        peca.setId(id);
        return peca;
    }
}
