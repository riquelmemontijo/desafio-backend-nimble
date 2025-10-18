package br.com.nimblebaas.cobranca.dto;

import java.math.BigDecimal;

public record CriarCobrancaDTO(String cpfDestinatario,
                               BigDecimal valor,
                               String descricao) {
}
