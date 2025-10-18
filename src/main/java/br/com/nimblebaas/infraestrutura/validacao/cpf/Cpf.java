package br.com.nimblebaas.infraestrutura.validacao.cpf;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CpfValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cpf {
    String message() default "{br.com.nimblebaas.validacao.Cpf.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
