package br.com.nimblebaas.infraestrutura.validacao.numcartao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartaoDeCreditoValidatorTest {
    
    private CartaoDeCreditoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CartaoDeCreditoValidator();
    }

    @Test
    @DisplayName("Deve retornar true para número de cartão válido")
    void deveRetornarTrueParaNumeroDeCartaoValido() {
        assertTrue(validator.isValid("1234-5678-9012-3456", null));
        assertTrue(validator.isValid("1234567890123456", null));
        assertTrue(validator.isValid("1234 5678 9012 3456", null));
        assertTrue(validator.isValid("1234567890123", null)); // Mínimo de 13 dígitos
        assertTrue(validator.isValid("1234567890123456789", null)); // Máximo de 19 dígitos
    }

    @Test
    @DisplayName("Deve retornar false para número de cartão nulo")
    void deveRetornarFalseParaNumeroDeCartaoNulo() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    @DisplayName("Deve retornar false para número de cartão vazio")
    void deveRetornarFalseParaNumeroDeCartaoVazio() {
        assertFalse(validator.isValid("", null));
    }

    @Test
    @DisplayName("Deve retornar false para número de cartão com caracteres não numéricos")
    void deveRetornarFalseParaNumeroDeCartaoComCaracteresNaoNumericos() {
        assertFalse(validator.isValid("123A-5678-9012-3456", null));
        assertFalse(validator.isValid("abcde", null));
    }

    @Test
    @DisplayName("Deve retornar false para número de cartão com menos de 13 dígitos")
    void deveRetornarFalseParaNumeroDeCartaoComMenosDe13Digitos() {
        assertFalse(validator.isValid("123456789012", null));
    }

    @Test
    @DisplayName("Deve retornar false para número de cartão com mais de 19 dígitos")
    void deveRetornarFalseParaNumeroDeCartaoComMaisDe19Digitos() {
        assertFalse(validator.isValid("12345678901234567890", null));
    }

}