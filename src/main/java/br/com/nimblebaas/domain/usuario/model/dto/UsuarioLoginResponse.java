package br.com.nimblebaas.domain.usuario.model.dto;

public record UsuarioLoginResponse(String token, Long expiresIn) {
}
