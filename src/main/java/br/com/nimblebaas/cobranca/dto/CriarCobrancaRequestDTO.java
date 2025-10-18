package br.com.nimblebaas.cobranca.dto;

import java.math.BigDecimal;

public record CriarCobrancaRequestDTO(String cpfDestinatario,
                                      BigDecimal valor,
                                      String descricao) {
}
