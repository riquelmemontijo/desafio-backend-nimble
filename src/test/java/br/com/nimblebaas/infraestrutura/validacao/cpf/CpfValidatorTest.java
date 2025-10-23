package br.com.nimblebaas.infraestrutura.validacao.cpf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CpfValidatorTest {

    private CpfValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CpfValidator();
    }

    @Test
    @DisplayName("Deve retornar true para CPF válido")
    void deveRetornarTrueParaCpfValido() {
        assertTrue(validator.isValid("673.602.280-00", null));
        assertTrue(validator.isValid("67360228000", null));
    }

    @Test
    @DisplayName("Deve retornar false para CPF nulo")
    void deveRetornarFalseParaCpfNulo() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    @DisplayName("Deve retornar false para CPF vazio")
    void deveRetornarFalseParaCpfVazio() {
        assertFalse(validator.isValid("", null));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com menos de 11 dígitos")
    void deveRetornarFalseParaCpfComMenosDe11Digitos() {
        assertFalse(validator.isValid("6736022800", null));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com mais de 11 dígitos")
    void deveRetornarFalseParaCpfComMaisDe11Digitos() {
        assertFalse(validator.isValid("6736022800000", null));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com todos os dígitos iguais")
    void deveRetornarFalseParaCpfComTodosDigitosIguais() {
        assertFalse(validator.isValid("11111111111", null));
        assertFalse(validator.isValid("22222222222", null));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com primeiro dígito verificador inválido")
    void deveRetornarFalseParaCpfComPrimeiroDigitoVerificadorInvalido() {
        assertFalse(validator.isValid("12345678911", null)); // O correto seria 00
    }

    @Test
    @DisplayName("Deve retornar false para CPF com segundo dígito verificador inválido")
    void deveRetornarFalseParaCpfComSegundoDigitoVerificadorInvalido() {
        assertFalse(validator.isValid("12345678912", null)); // O correto seria 00
    }

}