package com.baratieri.automasterbaratieri.service;

import com.baratieri.automasterbaratieri.dto.response.OrdemServicoResponseDTO;
import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.entities.Veiculo;
import com.baratieri.automasterbaratieri.enums.StatusOS;
import com.baratieri.automasterbaratieri.repositories.ItemPecaRepository;
import com.baratieri.automasterbaratieri.repositories.ItemServicoRepository;
import com.baratieri.automasterbaratieri.repositories.OrdemServicoRepository;
import com.baratieri.automasterbaratieri.services.*;
import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @InjectMocks
    private OrdemServicoService ordemServicoService;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;
    @Mock
    private ItemServicoRepository itemServicoRepository;
    @Mock
    private ItemPecaRepository itemPecaRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private ServicoService servicoService;
    @Mock
    private VeiculoService veiculoService;
    @Mock
    private PecaService pecaService;
    @Mock
    private MecanicoService mecanicoService;


    @Test
    @DisplayName("Deve finalizar a Ordem de Serviço com sucesso quando status for EM_EXECUCAO")
    void deveFinalizarOrdemServicoComSucesso() {

        Long osId = 1L;
        OrdemServico osFake = criarOsFake(StatusOS.EM_EXECUCAO);

        when(ordemServicoRepository.findById(osId)).thenReturn(Optional.of(osFake));
        when(ordemServicoRepository.save(any(OrdemServico.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        OrdemServicoResponseDTO responseDTO = ordemServicoService.finalizarOrdemServico(osId);

        assertNotNull(responseDTO);
        assertEquals(StatusOS.FINALIZADO, responseDTO.status());
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar finalizar uma O.S. já finalizada")
    void deveLancarExcecaoAoFinalizarOsJaFinalizada() {

        Long osId = 1L;
        OrdemServico osJaFinalizada = criarOsFake(StatusOS.FINALIZADO);

        when(ordemServicoRepository.findById(osId)).thenReturn(Optional.of(osJaFinalizada));

        RegraNegocioException excecao = assertThrows(
                RegraNegocioException.class,
                () -> ordemServicoService.finalizarOrdemServico(osId)
        );

        assertTrue(excecao.getMessage().contains("EM_EXECUCAO"),
                "A mensagem da exceção deveria citar EM_EXECUCAO, mas foi: " + excecao.getMessage());

        verify(ordemServicoRepository, never()).save(any(OrdemServico.class));
    }

    private OrdemServico criarOsFake(StatusOS status) {
        Cliente clienteFalso = new Cliente();
        clienteFalso.setNome("CLIENTE TESTE");

        Veiculo veiculoFalso = new Veiculo();
        veiculoFalso.setPlaca("ABC-1234");
        veiculoFalso.setModelo("Corolla");
        veiculoFalso.setMarca("Toyota");
        veiculoFalso.setCliente(clienteFalso);

        OrdemServico os = new OrdemServico();
        os.setId(1L);
        os.setStatus(status);
        os.setItensPeca(new ArrayList<>());
        os.setItensServico(new ArrayList<>());
        os.setVeiculo(veiculoFalso);
        return os;
    }
}
