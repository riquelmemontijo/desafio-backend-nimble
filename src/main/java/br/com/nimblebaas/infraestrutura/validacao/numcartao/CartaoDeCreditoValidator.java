package br.com.nimblebaas.infraestrutura.validacao.numcartao;

import br.com.nimblebaas.cobranca.dto.CartaoDeCreditoDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CartaoDeCreditoValidator implements ConstraintValidator<CartaoCredito, String> {

    @Override
    public boolean isValid(String numero, ConstraintValidatorContext context) {
        if (numero == null || numero.isBlank()) {
            return false;
        }

        String numeroLimpo = numero.replaceAll("[\\s-]", "");

        if (!numeroLimpo.matches("\\d+")) {
            return false;
        }

        return numeroLimpo.length() >= 13 && numeroLimpo.length() <= 19;
    }
}
