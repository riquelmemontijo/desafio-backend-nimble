package br.com.nimblebaas.domain.usuario.model.dto;

public record UsuarioLoginResponseDTO(String token, Long expiresIn) {
}
