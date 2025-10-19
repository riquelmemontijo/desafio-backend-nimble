package br.com.nimblebaas.infraestrutura.validacao.numcartao;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CartaoDeCreditoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CartaoCredito {

    String message() default "O número do cartão de crédito informado é inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
