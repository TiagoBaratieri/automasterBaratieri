package com.baratieri.automasterbaratieri.service;

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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;

@ExtendWith(MockitoExtension.class)
class PecaServiceTest {

    @InjectMocks
    private PecaService pecaService;

    @Mock
    private PecaRepository pecaRepository;

    @Test
    @DisplayName("Deve registrar Peças e salvar no banco de dados com sucesso")
    void deveSalvarPecaComSucesso(){

        PecaRequestDTO requestDTO = fabicaPecaRequestDto();

        when(pecaRepository.existsBySku(anyString())).thenReturn(false);
        when(pecaRepository.existsByPartNumber(anyString())).thenReturn(false);

        when(pecaRepository.save(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PecaResponseDTO responseDTO = pecaService.salvarPeca(requestDTO);

        assertNotNull(responseDTO);
        assertEquals("SKU123", responseDTO.sku());

        verify(pecaRepository, times(1)).save(any(Peca.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar Peça quando o SKU já existir no banco")
    void deveLancarExcecaoAoSalvarQuandoSkuJaExistir() {

        PecaRequestDTO requestDTO = fabicaPecaRequestDto();

        when(pecaRepository.existsBySku(anyString())).thenReturn(true);

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            pecaService.salvarPeca(requestDTO);
        });

        assertEquals("Já existe uma peça cadastrada com o SKU: SKU123", exception.getMessage());

        verify(pecaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException Quando a peça naõ existir.")
    void deveLancarResourceNotFoundExceptionQuandoPecaNaoExistir() {
        Long pecaIdInvalida = 999L;

        when(pecaRepository.findById(pecaIdInvalida)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            pecaService.buscarPecaPorId(pecaIdInvalida);
        });

        assertEquals("Peça não encontrada com ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Deve inativar a Peça com sucesso ao invés de deletar do banco")
    void deveInativarPecaComSucesso(){
        Long pecaId = 999L;
        Peca peca = new Peca();
        peca.setId(pecaId);

        when(pecaRepository.findById(pecaId)).thenReturn(java.util.Optional.of(peca));

        pecaService.ExcluirPeca(pecaId);


    }
    private PecaRequestDTO fabicaPecaRequestDto() {
        return new PecaRequestDTO(
                "SKU123",
                "Peça de teste",
                "PN123",
                "Bosch",
                "Gol 1.0",
                new BigDecimal("150.00"),
                new BigDecimal("100.00"),
                20,
                10
        );
    }
}


