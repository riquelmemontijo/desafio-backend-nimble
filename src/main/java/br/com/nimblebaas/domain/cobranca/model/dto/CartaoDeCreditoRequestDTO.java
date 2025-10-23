package br.com.nimblebaas.domain.cobranca.model.dto;

import br.com.nimblebaas.infraestrutura.validacao.numcartao.CartaoCredito;
import jakarta.validation.constraints.Pattern;

public record CartaoDeCreditoRequestDTO(@CartaoCredito
                                 String numero,
                                        @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$")
                                 String dataValidade,
                                        @Pattern(regexp = "^\\d{3,4}$")
                                 String cvv) {
}
