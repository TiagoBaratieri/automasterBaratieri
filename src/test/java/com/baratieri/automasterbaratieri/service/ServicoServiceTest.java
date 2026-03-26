package com.baratieri.automasterbaratieri.service;

import com.baratieri.automasterbaratieri.dto.request.ServicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.ServicoResponseDTO;
import com.baratieri.automasterbaratieri.entities.Servico;
import com.baratieri.automasterbaratieri.repositories.ServicoRepository;
import com.baratieri.automasterbaratieri.services.ServicoService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @InjectMocks
    private ServicoService servicoService;

    @Mock
    private ServicoRepository servicoRepository;

    // =========================================================================
    // salvarServico
    // =========================================================================

    @Test
    @DisplayName("Deve salvar serviço com sucesso quando dados forem válidos")
    void deveSalvarServicoComSucessoQuandoDadosForemValidos() {
        // ARRANGE
        ServicoRequestDTO dto = fabricarServicoRequestDTO("Troca de Óleo", new BigDecimal("150.00"));

        when(servicoRepository.existsByDescricaoIgnoreCase("Troca de Óleo")).thenReturn(false);
        when(servicoRepository.save(any(Servico.class))).thenAnswer(invocation -> {
            Servico s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        // ACT
        ServicoResponseDTO responseDTO = servicoService.salvarServico(dto);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id());
        // Servico.preencherDados → formatarTextoOpcional → trim().toUpperCase()
        assertEquals("TROCA DE ÓLEO", responseDTO.descricao());
        assertEquals(new BigDecimal("150.00"), responseDTO.valorMaoDeObraBase());
        verify(servicoRepository, times(1)).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException e não salvar quando descrição já existir no catálogo")
    void deveLancarExcecaoAoSalvarServicoComDescricaoDuplicada() {
        // ARRANGE
        ServicoRequestDTO dto = fabricarServicoRequestDTO("Troca de Óleo", new BigDecimal("150.00"));
        when(servicoRepository.existsByDescricaoIgnoreCase("Troca de Óleo")).thenReturn(true);

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> servicoService.salvarServico(dto));

        assertEquals("Já existe um serviço cadastrado com a descrição: Troca de Óleo",
                excecao.getMessage());
        verify(servicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando descrição for nula — validação de domínio na entidade")
    void deveLancarExcecaoAoSalvarServicoComDescricaoNula() {
        // ARRANGE
        // Descrição nula passa pela validarDescricaoExiste (existsByDescricaoIgnoreCase(null) → false)
        // mas falha no domínio: Servico.preencherDados → validarDadosObrigatorio(null, ...)
        ServicoRequestDTO dto = fabricarServicoRequestDTO(null, new BigDecimal("100.00"));
        when(servicoRepository.existsByDescricaoIgnoreCase(null)).thenReturn(false);

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> servicoService.salvarServico(dto));

        assertEquals("A descrição do serviço é obrigatória.", excecao.getMessage());
        verify(servicoRepository, never()).save(any());
    }

    // =========================================================================
    // buscarServicoPorId
    // =========================================================================

    @Test
    @DisplayName("Deve retornar ServicoResponseDTO correto ao buscar serviço por ID existente")
    void deveBuscarServicoPorIdComSucesso() {
        // ARRANGE
        Long servicoId = 1L;
        Servico servicoFake = fabricarServicoEntidade(servicoId);
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servicoFake));

        // ACT
        ServicoResponseDTO responseDTO = servicoService.buscarServicoPorId(servicoId);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals(servicoId, responseDTO.id());
        assertEquals("TROCA DE ÓLEO", responseDTO.descricao());
        assertEquals(new BigDecimal("150.00"), responseDTO.valorMaoDeObraBase());
        verify(servicoRepository, times(1)).findById(servicoId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException com mensagem exata quando ID não existir")
    void deveLancarResourceNotFoundExceptionQuandoServicoNaoExistir() {
        // ARRANGE
        Long idInexistente = 999L;
        when(servicoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> servicoService.buscarServicoPorId(idInexistente));

        assertEquals("Serviço não encontrado no catálogo com ID: 999", excecao.getMessage());
    }

    // =========================================================================
    // buscarServicos
    // =========================================================================

    @Test
    @DisplayName("Deve retornar Page<ServicoResponseDTO> mapeado ao buscar com filtro de descrição")
    void deveBuscarServicosComFiltroERetornarPaginacaoMapeada() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Servico servicoFake = fabricarServicoEntidade(1L);
        Page<Servico> paginaFake = new PageImpl<>(List.of(servicoFake), pageable, 1);

        when(servicoRepository.buscarServicosComFiltros("troca", pageable))
                .thenReturn(paginaFake);

        // ACT
        Page<ServicoResponseDTO> resultado = servicoService.buscarServicos("troca", pageable);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("TROCA DE ÓLEO", resultado.getContent().get(0).descricao());
        verify(servicoRepository, times(1))
                .buscarServicosComFiltros("troca", pageable);
    }

    @Test
    @DisplayName("Deve repassar null ao repositório quando filtro de descrição não for informado")
    void deveRepassarNullAoRepositorioQuandoFiltroDescricaoForNulo() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<Servico> paginaVazia = new PageImpl<>(List.of(), pageable, 0);
        // A query JPQL trata :descricao IS NULL retornando todos os registros ativos
        when(servicoRepository.buscarServicosComFiltros(null, pageable))
                .thenReturn(paginaVazia);

        // ACT
        Page<ServicoResponseDTO> resultado = servicoService.buscarServicos(null, pageable);

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(servicoRepository).buscarServicosComFiltros(null, pageable);
    }

    // =========================================================================
    // atualizarServico
    // =========================================================================

    @Test
    @DisplayName("Deve atualizar serviço com sucesso sem chamar save() — JPA dirty-checking")
    void deveAtualizarServicoComSucessoQuandoDadosForemValidos() {
        // ARRANGE
        Long servicoId = 1L;
        Servico servicoFake = fabricarServicoEntidade(servicoId);
        ServicoRequestDTO dto = fabricarServicoRequestDTO(
                "Alinhamento e Balanceamento", new BigDecimal("200.00"));

        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servicoFake));
        when(servicoRepository.existsByDescricaoIgnoreCase("Alinhamento e Balanceamento"))
                .thenReturn(false);

        // ACT
        ServicoResponseDTO responseDTO = servicoService.atualizarServico(servicoId, dto);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals("ALINHAMENTO E BALANCEAMENTO", responseDTO.descricao());
        assertEquals(new BigDecimal("200.00"), responseDTO.valorMaoDeObraBase());
        // CRÍTICO: atualizarServico depende de @Transactional (dirty-checking),
        // portanto save() NÃO deve ser chamado
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar serviço com ID inexistente")
    void deveLancarExcecaoAoAtualizarServicoComIdInexistente() {
        // ARRANGE
        Long idInexistente = 999L;
        ServicoRequestDTO dto = fabricarServicoRequestDTO("Revisão Geral", new BigDecimal("300.00"));
        when(servicoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> servicoService.atualizarServico(idInexistente, dto));

        assertEquals("Serviço não encontrado no catálogo com ID: 999", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao atualizar com descrição já existente — entidade não é alterada")
    void deveLancarExcecaoAoAtualizarServicoComDescricaoDuplicada() {
        // ARRANGE
        Long servicoId = 1L;
        Servico servicoFake = fabricarServicoEntidade(servicoId);
        String descricaoOriginal = servicoFake.getDescricao(); // "TROCA DE ÓLEO"

        ServicoRequestDTO dto = fabricarServicoRequestDTO("Troca de Filtro", new BigDecimal("80.00"));

        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servicoFake));
        when(servicoRepository.existsByDescricaoIgnoreCase("Troca de Filtro")).thenReturn(true);

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> servicoService.atualizarServico(servicoId, dto));

        assertEquals("Já existe um serviço cadastrado com a descrição: Troca de Filtro",
                excecao.getMessage());
        // Garante que a entidade NÃO foi alterada antes da exceção
        assertEquals(descricaoOriginal, servicoFake.getDescricao(),
                "A descrição da entidade não deve ser alterada quando a validação falha");
        verify(servicoRepository, never()).save(any());
    }

    // =========================================================================
    // excluirServico
    // =========================================================================

    @Test
    @DisplayName("Deve inativar logicamente o serviço (ativo = false) e chamar save() exatamente 1 vez")
    void deveInativarServicoLogicamenteComSucessoAoExcluir() {
        // ARRANGE
        Long servicoId = 1L;
        Servico servicoFake = fabricarServicoEntidade(servicoId);
        assertTrue(servicoFake.getAtivo(), "Pré-condição: serviço deve iniciar como ativo");
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servicoFake));
        when(servicoRepository.save(any(Servico.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        servicoService.excluirServico(servicoId);

        // ASSERT
        assertFalse(servicoFake.getAtivo(),
                "O serviço deve estar inativo (ativo = false) após exclusão lógica");
        verify(servicoRepository, times(1)).save(servicoFake);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException e não tocar no banco ao excluir ID inexistente")
    void deveLancarExcecaoAoExcluirServicoComIdInexistente() {
        // ARRANGE
        Long idInexistente = 999L;
        when(servicoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> servicoService.excluirServico(idInexistente));

        assertEquals("Serviço não encontrado no catálogo com ID: 999", excecao.getMessage());
        verify(servicoRepository, never()).save(any());
    }

    // =========================================================================
    // Fábricas de Dados (Test Fixtures)
    // =========================================================================

    /**
     * Cria um DTO de requisição com a descrição e valor informados.
     * O campo 'id' do record é null — simulando uma criação nova.
     */
    private ServicoRequestDTO fabricarServicoRequestDTO(String descricao, BigDecimal valor) {
        return new ServicoRequestDTO(null, descricao, valor);
    }

    /**
     * Entidade Servico preenchida via preencherDados() (validações de domínio aplicadas).
     * Valores pós-construção:
     * - descricao         → "TROCA DE ÓLEO" (formatarTextoOpcional → toUpperCase)
     * - valorMaoDeObraBase → 150.00
     * - ativo              → true (padrão de Inativavel)
     */
    private Servico fabricarServicoEntidade(Long id) {
        Servico servico = new Servico();
        servico.preencherDados("Troca de Óleo", new BigDecimal("150.00"));
        servico.setId(id);
        return servico;
    }
}
