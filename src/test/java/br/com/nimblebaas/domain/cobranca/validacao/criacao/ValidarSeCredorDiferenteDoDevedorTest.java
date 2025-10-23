package br.com.nimblebaas.domain.cobranca.validacao.criacao;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidarSeCredorDiferenteDoDevedorTest {

    private ValidarSeCredorDiferenteDoDevedor validador;

    @BeforeEach
    void setUp() {
        validador = new ValidarSeCredorDiferenteDoDevedor();
    }

    @Test
    @DisplayName("Deve lançar exceção quando credor e devedor forem o mesmo")
    void deveLancarExcecaoQuandoCredorEDevedorSaoOMesmo() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Cobranca cobranca = new Cobranca();
        cobranca.setOriginador(usuario);
        cobranca.setDestinatario(usuario);

        // Act & Assert
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () ->
                validador.validar(cobranca));

        assertEquals("Credor deve ser diferente do devedor", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando credor e devedor forem diferentes")
    void naoDeveLancarExcecaoQuandoCredorEDevedorSaoDiferentes() {
        // Arrange
        Usuario credor = new Usuario();
        credor.setId(1L);

        Usuario devedor = new Usuario();
        devedor.setId(2L);

        Cobranca cobranca = new Cobranca();
        cobranca.setOriginador(credor);
        cobranca.setDestinatario(devedor);

        // Act & Assert
        assertDoesNotThrow(() -> validador.validar(cobranca));
    }


}