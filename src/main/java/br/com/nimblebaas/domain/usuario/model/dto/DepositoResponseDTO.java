package br.com.nimblebaas.domain.usuario.model.dto;

import java.math.BigDecimal;

public record DepositoResponseDTO(String nomeUsuario, String cpf, BigDecimal valor, String mensagem) {
}
