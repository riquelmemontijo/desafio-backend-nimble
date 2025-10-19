package br.com.nimblebaas.cobranca.dto;

import java.time.LocalDate;

public record CartaoDeCreditoDTO(String numero,
                                 LocalDate validade,
                                 String cvv) {
}
