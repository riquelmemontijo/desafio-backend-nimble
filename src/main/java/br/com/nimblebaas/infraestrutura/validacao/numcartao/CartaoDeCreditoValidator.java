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

        // Remove espaços e hífens
        String numeroLimpo = numero.replaceAll("[\\s-]", "");

        // Verifica se contém apenas dígitos
        if (!numeroLimpo.matches("\\d+")) {
            return false;
        }

        // Verifica se tem entre 13 e 19 dígitos (tamanho padrão de cartões)
        if (numeroLimpo.length() < 13 || numeroLimpo.length() > 19) {
            return false;
        }

        // Aplica o algoritmo de Luhn
        return validarAlgoritmoLuhn(numeroLimpo);
    }

    private boolean validarAlgoritmoLuhn(String numero) {
        int soma = 0;
        boolean alternar = false;

        // Percorre o número de trás para frente
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(numero.charAt(i));

            if (alternar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }

            soma += digito;
            alternar = !alternar;
        }

        return (soma % 10) == 0;
    }
}
