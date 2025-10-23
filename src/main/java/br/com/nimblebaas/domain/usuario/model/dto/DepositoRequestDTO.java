package br.com.nimblebaas.domain.usuario.model.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositoRequestDTO(@Positive
                                 @NotNull
                                 @Digits(integer = 10, fraction = 2)
                                 BigDecimal valor) {
}
