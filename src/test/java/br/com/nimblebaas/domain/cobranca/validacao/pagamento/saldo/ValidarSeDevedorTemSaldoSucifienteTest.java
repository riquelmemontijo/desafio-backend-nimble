package br.com.nimblebaas.domain.cobranca.validacao.pagamento.saldo;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidarSeDevedorTemSaldoSucifienteTest {

    private ValidarSeDevedorTemSaldoSucifiente validador;

    @BeforeEach
    void setUp() {
        validador = new ValidarSeDevedorTemSaldoSucifiente();
    }

    @Test
    @DisplayName("Deve lançar exceção quando o devedor não tem saldo suficiente")
    void deveLancarExcecaoQuandoDevedorNaoTemSaldoSuficiente() {
        // arrange
        Usuario devedor = new Usuario();
        devedor.creditarSaldo(BigDecimal.valueOf(50.00));

        Cobranca cobranca = new Cobranca();
        cobranca.setValor(BigDecimal.valueOf(100.00));
        cobranca.setDestinatario(devedor);

        // act and assert
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () ->
                validador.validar(cobranca));

        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando o devedor tem saldo suficiente")
    void naoDeveLancarExcecaoQuandoDevedorTemSaldoSuficiente() {
        // arrange
        Usuario devedor = new Usuario();
        devedor.creditarSaldo(BigDecimal.valueOf(100.00));

        Cobranca cobranca = new Cobranca();
        cobranca.setValor(BigDecimal.valueOf(100.00));
        cobranca.setDestinatario(devedor);

        // act and assert
        assertDoesNotThrow(() -> validador.validar(cobranca));
    }

    @Test
    @DisplayName("Não deve lançar exceção quando o devedor tem mais saldo do que o valor da cobrança")
    void naoDeveLancarExcecaoQuandoDevedorTemSaldoMaiorQueCobranca() {
        // arrange
        Usuario devedor = new Usuario();
        devedor.creditarSaldo(BigDecimal.valueOf(150.00));

        Cobranca cobranca = new Cobranca();
        cobranca.setValor(BigDecimal.valueOf(100.00));
        cobranca.setDestinatario(devedor);

        // act and assert
        assertDoesNotThrow(() -> validador.validar(cobranca));
    }



}