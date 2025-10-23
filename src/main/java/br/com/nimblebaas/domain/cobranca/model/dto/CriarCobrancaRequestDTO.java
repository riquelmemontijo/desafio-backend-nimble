package br.com.nimblebaas.domain.cobranca.model.dto;

import br.com.nimblebaas.infraestrutura.validacao.cpf.Cpf;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CriarCobrancaRequestDTO(@Cpf
                                      String cpfDestinatario,
                                      @Positive
                                      @Digits(integer = 10, fraction = 2)
                                      BigDecimal valor,
                                      String descricao) {
}
