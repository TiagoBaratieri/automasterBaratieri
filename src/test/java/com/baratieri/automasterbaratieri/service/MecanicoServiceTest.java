package com.baratieri.automasterbaratieri.service;

import com.baratieri.automasterbaratieri.dto.request.AtualizarMecanicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.request.MecanicoRequestDTO;
import com.baratieri.automasterbaratieri.dto.response.MecanicoResponseDTO;
import com.baratieri.automasterbaratieri.entities.Mecanico;
import com.baratieri.automasterbaratieri.repositories.MecanicoRepository;
import com.baratieri.automasterbaratieri.services.MecanicoService;
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
class MecanicoServiceTest {

    @InjectMocks
    private MecanicoService mecanicoService;

    @Mock
    private MecanicoRepository mecanicoRepository;

    // =========================================================================
    // salvarMecanico
    // =========================================================================

    @Test
    @DisplayName("Deve salvar mecânico com sucesso limpando máscara do CPF e formatando campos em maiúsculo")
    void deveSalvarMecanicoComSucessoQuandoDadosForemValidos() {
        // ARRANGE
        // CPF com máscara → limparDocumento → "12345678909"
        MecanicoRequestDTO dto = fabricarMecanicoRequestDTO("123.456.789-09");

        when(mecanicoRepository.existsByCpf("12345678909")).thenReturn(false);
        when(mecanicoRepository.save(any(Mecanico.class))).thenAnswer(invocation -> {
            Mecanico m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        // ACT
        MecanicoResponseDTO responseDTO = mecanicoService.salvarMecanico(dto);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id());
        // Construtor da entidade faz formatarTextoOpcional → toUpperCase
        assertEquals("CARLOS SILVA", responseDTO.nome());
        assertEquals("MOTOR", responseDTO.especialidade());
        // CPF já limpo pelo serviço, formatarTextoOpcional não altera dígitos
        assertEquals("12345678909", responseDTO.cpf());
        assertEquals(new BigDecimal("10.00"), responseDTO.taxaComissao());
        assertTrue(responseDTO.ativo());
        verify(mecanicoRepository, times(1)).save(any(Mecanico.class));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException e não salvar quando CPF já estiver cadastrado")
    void deveLancarExcecaoAoSalvarMecanicoComCpfDuplicado() {
        // ARRANGE
        MecanicoRequestDTO dto = fabricarMecanicoRequestDTO("123.456.789-09");
        // Simula CPF já cadastrado após limpeza da máscara
        when(mecanicoRepository.existsByCpf("12345678909")).thenReturn(true);

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> mecanicoService.salvarMecanico(dto));

        // A mensagem deve conter o CPF ORIGINAL (com máscara) para orientar o usuário
        assertTrue(excecao.getMessage().contains("123.456.789-09"),
                "Mensagem deveria conter o CPF original com máscara, mas foi: " + excecao.getMessage());
        assertTrue(excecao.getMessage().contains("Mecânico"));
        verify(mecanicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando nome do mecânico for nulo")
    void deveLancarExcecaoAoSalvarMecanicoComNomeNulo() {
        // ARRANGE
        MecanicoRequestDTO dto = new MecanicoRequestDTO(
                null,               // nome nulo → construtor da entidade lança RegraNegocioException
                "12345678909",
                "Elétrica",
                new BigDecimal("12.00"),
                true
        );
        when(mecanicoRepository.existsByCpf(anyString())).thenReturn(false);

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> mecanicoService.salvarMecanico(dto));

        assertEquals("O nome do mecânico é obrigatório.", excecao.getMessage());
        verify(mecanicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando taxa de comissão for zero ou nula")
    void deveLancarExcecaoAoSalvarMecanicoComTaxaDeComissaoZero() {
        // ARRANGE
        // taxaComissao = ZERO → Mecanico.atualizarDados() → validarValorMaiorQueZero → lança exceção
        MecanicoRequestDTO dto = new MecanicoRequestDTO(
                "Carlos Silva",
                "12345678909",
                "Motor",
                BigDecimal.ZERO,    // inválido — deve ser > 0
                true
        );
        when(mecanicoRepository.existsByCpf(anyString())).thenReturn(false);

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> mecanicoService.salvarMecanico(dto));

        assertEquals("A taxa de comissão não pode ser nula. Ou negativa.", excecao.getMessage());
        verify(mecanicoRepository, never()).save(any());
    }

    // =========================================================================
    // buscarMecanicoPorId
    // =========================================================================

    @Test
    @DisplayName("Deve retornar MecanicoResponseDTO correto ao buscar mecânico por ID existente")
    void deveBuscarMecanicoPorIdComSucesso() {
        // ARRANGE
        Long mecanicoId = 1L;
        Mecanico mecanicoFake = fabricarMecanicoEntidade(mecanicoId);
        when(mecanicoRepository.findById(mecanicoId)).thenReturn(Optional.of(mecanicoFake));

        // ACT
        MecanicoResponseDTO responseDTO = mecanicoService.buscarMecanicoPorId(mecanicoId);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals(mecanicoId, responseDTO.id());
        assertEquals("CARLOS SILVA", responseDTO.nome());
        assertEquals("MOTOR", responseDTO.especialidade());
        verify(mecanicoRepository, times(1)).findById(mecanicoId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException com mensagem correta quando ID não existir")
    void deveLancarResourceNotFoundExceptionQuandoMecanicoNaoExistir() {
        // ARRANGE
        Long idInexistente = 999L;
        when(mecanicoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> mecanicoService.buscarMecanicoPorId(idInexistente));

        assertEquals("Mecânico não encontrada com ID: 999", excecao.getMessage());
    }

    // =========================================================================
    // buscarMecanico
    // =========================================================================

    @Test
    @DisplayName("Deve retornar Page<MecanicoResponseDTO> mapeado ao buscar mecânicos com filtros")
    void deveBuscarMecanicosComFiltrosERetornarPaginacaoMapeada() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Mecanico mecanicoFake = fabricarMecanicoEntidade(1L);
        Page<Mecanico> paginaFake = new PageImpl<>(List.of(mecanicoFake), pageable, 1);

        when(mecanicoRepository.buscarMecanicoComFiltro("carlos", "motor", true, pageable))
                .thenReturn(paginaFake);

        // ACT
        Page<MecanicoResponseDTO> resultado =
                mecanicoService.buscarMecanico("carlos", "motor", true, pageable);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("CARLOS SILVA", resultado.getContent().get(0).nome());
        assertTrue(resultado.getContent().get(0).ativo());
        verify(mecanicoRepository, times(1))
                .buscarMecanicoComFiltro("carlos", "motor", true, pageable);
    }

    // =========================================================================
    // atualizarMecanico
    // =========================================================================

    @Test
    @DisplayName("Deve atualizar taxa de comissão com sucesso e chamar save() — sem dirty-checking")
    void deveAtualizarMecanicoComSucessoQuandoTaxaForValida() {
        // ARRANGE
        Long mecanicoId = 1L;
        Mecanico mecanicoFake = fabricarMecanicoEntidade(mecanicoId);
        AtualizarMecanicoRequestDTO dto = new AtualizarMecanicoRequestDTO(
                new BigDecimal("15.00"), true
        );
        when(mecanicoRepository.findById(mecanicoId)).thenReturn(Optional.of(mecanicoFake));
        when(mecanicoRepository.save(any(Mecanico.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        MecanicoResponseDTO responseDTO = mecanicoService.atualizarMecanico(mecanicoId, dto);

        // ASSERT
        assertNotNull(responseDTO);
        assertEquals(new BigDecimal("15.00"), responseDTO.taxaComissao());
        // CRÍTICO: diferente de atualizarCliente/atualizarPeca,
        // atualizarMecanico chama save() explicitamente — não depende de dirty-checking
        verify(mecanicoRepository, times(1)).save(mecanicoFake);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao atualizar mecânico com taxa de comissão zero")
    void deveLancarExcecaoAoAtualizarMecanicoComTaxaDeComissaoZero() {
        // ARRANGE
        Long mecanicoId = 1L;
        Mecanico mecanicoFake = fabricarMecanicoEntidade(mecanicoId);
        AtualizarMecanicoRequestDTO dtoInvalido = new AtualizarMecanicoRequestDTO(
                BigDecimal.ZERO, true // ZERO → validarValorMaiorQueZero lança exceção no domínio
        );
        when(mecanicoRepository.findById(mecanicoId)).thenReturn(Optional.of(mecanicoFake));

        // ACT & ASSERT
        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> mecanicoService.atualizarMecanico(mecanicoId, dtoInvalido));

        assertEquals("A taxa de comissão não pode ser nula. Ou negativa.", excecao.getMessage());
        verify(mecanicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar mecânico com ID inexistente")
    void deveLancarExcecaoAoAtualizarMecanicoComIdInexistente() {
        // ARRANGE
        Long idInexistente = 999L;
        AtualizarMecanicoRequestDTO dto = new AtualizarMecanicoRequestDTO(
                new BigDecimal("10.00"), true
        );
        when(mecanicoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> mecanicoService.atualizarMecanico(idInexistente, dto));

        assertEquals("Mecânico não encontrada com ID: 999", excecao.getMessage());
    }

    // =========================================================================
    // excluirMecanico
    // =========================================================================

    @Test
    @DisplayName("Deve inativar logicamente o mecânico (ativo = false) e chamar save() exatamente 1 vez")
    void deveInativarMecanicoLogicamenteComSucessoAoExcluir() {
        // ARRANGE
        Long mecanicoId = 1L;
        Mecanico mecanicoFake = fabricarMecanicoEntidade(mecanicoId);
        assertTrue(mecanicoFake.getAtivo(), "Pré-condição: mecânico deve iniciar como ativo");
        when(mecanicoRepository.findById(mecanicoId)).thenReturn(Optional.of(mecanicoFake));
        when(mecanicoRepository.save(any(Mecanico.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        mecanicoService.excluirMecanico(mecanicoId);

        // ASSERT
        assertFalse(mecanicoFake.getAtivo(),
                "O mecânico deve estar inativo (ativo = false) após a exclusão lógica");
        verify(mecanicoRepository, times(1)).save(mecanicoFake);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException e não tocar no banco ao excluir ID inexistente")
    void deveLancarExcecaoAoExcluirMecanicoComIdInexistente() {
        // ARRANGE
        Long idInexistente = 999L;
        when(mecanicoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class,
                () -> mecanicoService.excluirMecanico(idInexistente));

        assertEquals("Mecânico não encontrada com ID: 999", excecao.getMessage());
        verify(mecanicoRepository, never()).save(any());
    }

    // =========================================================================
    // Fábricas de Dados (Test Fixtures)
    // =========================================================================

    /**
     * O parâmetro cpf aceita valores com ou sem máscara para testar
     * o comportamento de limpeza do FormatacaoUtil no serviço.
     */
    private MecanicoRequestDTO fabricarMecanicoRequestDTO(String cpf) {
        return new MecanicoRequestDTO(
                "Carlos Silva",
                cpf,
                "Motor",
                new BigDecimal("10.00"),
                true
        );
    }

    /**
     * Entidade construída via construtor de domínio (validações aplicadas).
     * Valores pós-construção:
     * - nome        → "CARLOS SILVA"  (formatarTextoOpcional → toUpperCase)
     * - cpf         → "12345678909"   (já limpo, sem máscara)
     * - especialidade → "MOTOR"
     * - taxaComissao → 10.00
     * - ativo       → true (padrão de Inativavel)
     */
    private Mecanico fabricarMecanicoEntidade(Long id) {
        Mecanico mecanico = new Mecanico(
                "Carlos Silva",
                "12345678909",
                "Motor",
                new BigDecimal("10.00")
        );
        mecanico.setId(id);
        return mecanico;
    }
}
