package br.com.nimblebaas.domain.cobranca.validacao.pagamento.geral;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import br.com.nimblebaas.util.usuario.UsuarioUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidarSeUsuarioPagadorIgualDevedorTest {

    private ValidarSeUsuarioPagadorIgualDevedor validador;
    @Mock
    private UsuarioUtils usuarioUtils;

    @BeforeEach
    void setUp() {
        validador = new ValidarSeUsuarioPagadorIgualDevedor(usuarioUtils);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário logado não for o devedor da cobrança")
    void deveLancarExcecaoQuandoUsuarioLogadoNaoForDevedor() {
        // arrange
        Usuario devedorCobranca = new Usuario();
        devedorCobranca.setId(1L);

        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(2L);

        Cobranca cobranca = new Cobranca();
        cobranca.setDestinatario(devedorCobranca);

        when(usuarioUtils.getUsuarioLogado()).thenReturn(usuarioLogado);

        // act and assert
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () ->
                validador.validar(cobranca));

        assertEquals("O solicitande não é o devedor desta cobrança.", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando o usuário logado for o devedor da cobrança")
    void naoDeveLancarExcecaoQuandoUsuarioLogadoForDevedor() {
        // arrange
        Usuario devedorCobranca = new Usuario();
        devedorCobranca.setId(1L);

        Cobranca cobranca = new Cobranca();
        cobranca.setDestinatario(devedorCobranca);

        when(usuarioUtils.getUsuarioLogado()).thenReturn(devedorCobranca);

        // act & assert
        assertDoesNotThrow(() -> validador.validar(cobranca));
    }


}