package br.com.nimblebaas.usuario.dto;

public record UsuarioLoginResponse(String token, Long expiresIn) {
}
