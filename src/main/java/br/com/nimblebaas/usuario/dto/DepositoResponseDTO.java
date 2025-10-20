package br.com.nimblebaas.usuario.dto;

import java.math.BigDecimal;

public record DepositoResponseDTO(String nomeUsuario, String cpf, BigDecimal valor, String mensagem) {
}
