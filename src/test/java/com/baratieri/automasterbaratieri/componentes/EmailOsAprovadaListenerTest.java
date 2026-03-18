package com.baratieri.automasterbaratieri.componentes;

import com.baratieri.automasterbaratieri.entities.Cliente;
import com.baratieri.automasterbaratieri.entities.OrdemServico;
import com.baratieri.automasterbaratieri.entities.Veiculo;
import com.baratieri.automasterbaratieri.eventos.OrdemServicoAprovadaEvento;
import com.baratieri.automasterbaratieri.services.RelatorioService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailOsAprovadaListenerTest {

    @InjectMocks
    private EmailOsAprovadaListener emailListener;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private RelatorioService relatorioService;

    @Test
    @DisplayName("Deve montar e enviar o e-mail com o PDF em anexo quando a O.S. for aprovada")
    void deveEnviarEmailComPdfAnexadoComSucesso() {

        ReflectionTestUtils.setField(emailListener, "emailDestinatario", "contato@automaster.com");
        Cliente clienteFalso = new Cliente();
        clienteFalso.setEmail("cliente@teste.com");
        clienteFalso.setNome("Senhor Miyagi");

        Veiculo veiculoFalso = new Veiculo();
        veiculoFalso.setCliente(clienteFalso);

        OrdemServico os = new OrdemServico();
        os.setId(1L);
        os.setVeiculo(veiculoFalso);

        OrdemServicoAprovadaEvento evento = new OrdemServicoAprovadaEvento(os);

        MimeMessage mimeMessageVazio = new MimeMessage((Session) null);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessageVazio);
        when(relatorioService.gerarPdfOrdemServico(os.getId())).thenReturn(new byte[]{1, 2, 3}); // PDF falso de 3 bytes

        emailListener.enviarPdfAoCliente(evento);

        verify(relatorioService, times(1)).gerarPdfOrdemServico(os.getId());

        verify(mailSender, times(1)).send(mimeMessageVazio);
    }
}