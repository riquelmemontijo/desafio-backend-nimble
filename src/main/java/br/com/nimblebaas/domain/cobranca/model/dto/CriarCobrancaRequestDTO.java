package br.com.nimblebaas.domain.cobranca.model.dto;

import java.math.BigDecimal;

public record CriarCobrancaRequestDTO(String cpfDestinatario,
                                      BigDecimal valor,
                                      String descricao) {
}
