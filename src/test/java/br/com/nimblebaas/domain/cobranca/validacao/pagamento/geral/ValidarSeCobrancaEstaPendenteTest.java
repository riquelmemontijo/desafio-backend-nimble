package br.com.nimblebaas.domain.cobranca.validacao.pagamento.geral;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidarSeCobrancaEstaPendenteTest {

    private ValidarSeCobrancaEstaPendente validador;

    @BeforeEach
    void setUp() {
        validador = new ValidarSeCobrancaEstaPendente();
    }

    @Test
    @DisplayName("Deve lançar exceção quando a cobrança não estiver pendente")
    void deveLancarExcecaoQuandoCobrancaNaoEstiverPendente() {
        // Arrange
        Cobranca cobrancaPaga = new Cobranca();
        cobrancaPaga.setStatusCobranca(StatusCobranca.PAGA);

        Cobranca cobrancaCancelada = new Cobranca();
        cobrancaCancelada.setStatusCobranca(StatusCobranca.CANCELADA);

        // Act & Assert
        RegraDeNegocioException exceptionPaga = assertThrows(RegraDeNegocioException.class, () ->
                validador.validar(cobrancaPaga));
        assertEquals("Somente cobrancas pendentes podem ser pagas", exceptionPaga.getMessage());

        RegraDeNegocioException exceptionCancelada = assertThrows(RegraDeNegocioException.class, () ->
                validador.validar(cobrancaCancelada));
        assertEquals("Somente cobrancas pendentes podem ser pagas", exceptionCancelada.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando a cobrança estiver pendente")
    void naoDeveLancarExcecaoQuandoCobrancaEstiverPendente() {
        // Arrange
        Cobranca cobrancaPendente = new Cobranca();
        cobrancaPendente.setStatusCobranca(StatusCobranca.PENDENTE);

        // Act & Assert
        assertDoesNotThrow(() -> validador.validar(cobrancaPendente));
    }


}