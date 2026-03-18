package com.baratieri.automasterbaratieri.util;

import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatacaoUtilTest {

    @Test
    @DisplayName("Deve limpar os espaços em branco e deixar o texto em maiúsculas")
    void deveFormatarTextoOpcional() {
        assertNull(FormatacaoUtil.formatarTextoOpcional(null));
        assertNull(FormatacaoUtil.formatarTextoOpcional("   "));

        // Simula um texto com espaços acidentais antes e depois
        assertEquals("SARANDI", FormatacaoUtil.formatarTextoOpcional("  sarandi  "));
    }

    @Test
    @DisplayName("Deve remover caracteres especiais da placa e deixar em maiúsculas")
    void deveLimparPlaca() {
        assertNull(FormatacaoUtil.limparPlaca(null));

        // Teste de placa no padrão antigo e com letras minúsculas
        assertEquals("ABC1234", FormatacaoUtil.limparPlaca("aBc-1234"));

        // Teste de placa no padrão Mercosul com espaços e traços
        assertEquals("MER1V4A", FormatacaoUtil.limparPlaca(" mer-1V4a "));
    }

    @Test
    @DisplayName("Deve remover tudo o que não for número do documento")
    void deveLimparDocumento() {
        assertNull(FormatacaoUtil.limparDocumento(null));

        // Teste com CPF
        assertEquals("11122233344", FormatacaoUtil.limparDocumento("111.222.333-44"));

        // Teste com CNPJ
        assertEquals("14866380000199", FormatacaoUtil.limparDocumento("14.866.380/0001-99"));
    }

    @Test
    @DisplayName("Deve formatar o email para minúsculas e remover espaços")
    void deveFormatarEmail() {
        assertNull(FormatacaoUtil.formatarEmail(null));
        assertNull(FormatacaoUtil.formatarEmail("   "));
        assertEquals("contato@oficina.com", FormatacaoUtil.formatarEmail(" Contato@Oficina.com  "));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando o documento já existir na base de dados")
    void deveLancarExcecaoQuandoDocumentoJaExistir() {

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            FormatacaoUtil.validarDocumentoUnico("111222", "111.222", "Cliente", doc -> true);
        });

        assertEquals("Já existe um(a) Cliente cadastrado(a) com este documento: 111.222", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando o documento for inédito")
    void naoDeveLancarExcecaoQuandoDocumentoForInedito() {

        assertDoesNotThrow(() -> {
            FormatacaoUtil.validarDocumentoUnico("111222", "111.222", "Cliente", doc -> false);
        });
    }
}