package br.com.nimblebaas.domain.cobranca.validacao.cancelamento;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidaSeCobrancaEstaCanceladaTest {

    private ValidaSeCobrancaEstaCancelada validaSeCobrancaEstaCancelada;

    @BeforeEach
    void setUp() {
        validaSeCobrancaEstaCancelada = new ValidaSeCobrancaEstaCancelada();
    }

    @Test
    @DisplayName("Deve lançar exceção se a cobrança já estiver cancelada")
    void deveLancarExcecaoSeCobrancaJaEstiverCancelada() {
        // Arrange
        Cobranca cobranca = new Cobranca();
        cobranca.setStatusCobranca(StatusCobranca.CANCELADA);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                validaSeCobrancaEstaCancelada.validar(cobranca));

        assertEquals("Cobrança já está cancelada", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção se a cobrança não estiver cancelada")
    void naoDeveLancarExcecaoSeCobrancaNaoEstiverCancelada() {
        // Arrange
        Cobranca cobrancaPendente = new Cobranca();
        cobrancaPendente.setStatusCobranca(StatusCobranca.PENDENTE);

        Cobranca cobrancaPaga = new Cobranca();
        cobrancaPaga.setStatusCobranca(StatusCobranca.PAGA);

        // Act & Assert
        assertDoesNotThrow(() -> validaSeCobrancaEstaCancelada.validar(cobrancaPendente));
        assertDoesNotThrow(() -> validaSeCobrancaEstaCancelada.validar(cobrancaPaga));
    }


}