package br.com.nimblebaas.domain.cobranca.validacao.pagamento.cartao;

import br.com.nimblebaas.domain.cobranca.model.dto.CartaoDeCreditoRequestDTO;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidaDataDeVencimentoDoCartaoTest {

    private ValidaDataDeVencimentoDoCartao validador;

    @BeforeEach
    void setUp() {
        validador = new ValidaDataDeVencimentoDoCartao();
    }

    @Test
    @DisplayName("Deve lançar exceção quando a data de validade do cartão estiver expirada")
    void deveLancarExcecaoQuandoDataDeValidadeExpirada() {
        // arrange
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/yy");
        String mesPassado = YearMonth.now().minusMonths(1).format(dtf);
        CartaoDeCreditoRequestDTO cartaoExpirado = new CartaoDeCreditoRequestDTO(
                "1234567890123456",
                mesPassado,
                "123"
        );

        // act and assert
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () ->
                validador.validar(cartaoExpirado));

        assertEquals("Cartão com data de validade expirada", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando a data de validade do cartão for maior que data atual")
    void naoDeveLancarExcecaoQuandoDataDeValidadeMaiorQueMesAtual() {
        // arrange
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/yy");
        String proximoMes = YearMonth.now().plusMonths(1).format(dtf);
        CartaoDeCreditoRequestDTO cartaoValido = new CartaoDeCreditoRequestDTO(
                "1234567890123456",
                proximoMes,
                "123"
        );

        // act and assert
        assertDoesNotThrow(() -> validador.validar(cartaoValido));
    }

    @Test
    @DisplayName("Não deve lançar exceção quando a data de validade do cartão for o mês atual")
    void naoDeveLancarExcecaoQuandoDataDeValidadeForMesAtual() {
        // arrange
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/yy");
        String mesAtual = YearMonth.now().format(dtf);
        CartaoDeCreditoRequestDTO cartaoValido = new CartaoDeCreditoRequestDTO(
                "1234567890123456",
                mesAtual,
                "123"
        );
        // act and assert
        assertDoesNotThrow(() -> validador.validar(cartaoValido));
    }

}