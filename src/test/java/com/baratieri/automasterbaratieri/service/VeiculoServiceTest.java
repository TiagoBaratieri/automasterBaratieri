package com.baratieri.automasterbaratieri.service;

import com.baratieri.automasterbaratieri.dto.request.VeiculoRequestDTO;
import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.entities.Veiculo;
import com.baratieri.automasterbaratieri.repositories.ClienteRepository;
import com.baratieri.automasterbaratieri.repositories.VeiculoRepository;
import com.baratieri.automasterbaratieri.services.ClienteService;
import com.baratieri.automasterbaratieri.services.VeiculoService;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VeiculoServiceTest {

    @InjectMocks
    private VeiculoService veiculoService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;


    @Test
    @DisplayName("Deve salvar Veículo com sucesso quando atrelado a um Cliente existente")
    void deveSalvarVeiculoComSucesso() {
        Long clienteId = 1L;
        VeiculoRequestDTO dto = fabricaVeiculoRequestDTO(clienteId);

        Cliente donoDoCarro = new Cliente();
        donoDoCarro.setId(clienteId);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(donoDoCarro));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(i -> i.getArgument(0));

        var response = veiculoService.salvarVeiculo(dto);

        assertNotNull(response);
        verify(veiculoRepository, times(1)).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao salvar o Veículo sem um Cliente existente")
    void deveLancarExcecaoAoSalvarVeiculoSemCliente() {
        Long clienteId = 99L;
        VeiculoRequestDTO requestDTO = fabricaVeiculoRequestDTO(clienteId);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            veiculoService.salvarVeiculo(requestDTO);
        });

        assertEquals("Cliente não encontrado com ID: 99", exception.getMessage());

        verify(veiculoRepository, never()).save(any());

    }

   private VeiculoRequestDTO fabricaVeiculoRequestDTO(Long clienteId) {
        return new VeiculoRequestDTO(
                "ABC-1234",
                "Fiat",
                "Uno",
                2010,
                clienteId
        );
   }
}
