package br.com.nimblebaas.domain.cobranca.validacao.cancelamento;

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
class ValidaSeOriginadorIgualQuemEstaCancelandoTest {

    @Mock
    private UsuarioUtils usuarioUtils;

    private ValidaSeOriginadorIgualQuemEstaCancelando validaSeOriginadorIgualQuemEstaCancelando;
    private Usuario usuarioCredor;
    private Usuario usuarioNaoCredor;


    @BeforeEach
    void setUp() {
        validaSeOriginadorIgualQuemEstaCancelando = new ValidaSeOriginadorIgualQuemEstaCancelando(usuarioUtils);

        usuarioCredor = new Usuario();
        usuarioCredor.setId(1L);

        usuarioNaoCredor = new Usuario();
        usuarioNaoCredor.setId(2L);

    }

    @Test
    @DisplayName("Deve permitir cancelar se o usuario logado for o credor da cobranca")
    void devePermitirCancelarSeUsuarioLogadoForCredor(){
        when(usuarioUtils.getUsuarioLogado())
                .thenReturn(usuarioCredor);

        Cobranca cobranca = new Cobranca();
        cobranca.setOriginador(usuarioCredor);

        assertDoesNotThrow(() -> validaSeOriginadorIgualQuemEstaCancelando.validar(cobranca));

    }

    @Test
    @DisplayName("Deve lançar exceção se o usuario logado não for o credor da cobranca")
    void deveLancarExcecaoSeUsuarioLogadoNaoForCredor(){
        when(usuarioUtils.getUsuarioLogado())
                .thenReturn(usuarioCredor);

        Cobranca cobranca = new Cobranca();
        cobranca.setOriginador(usuarioNaoCredor);

        assertThrows(RegraDeNegocioException.class, () -> validaSeOriginadorIgualQuemEstaCancelando.validar(cobranca));

    }

}