package br.com.nimblebaas.domain.usuario.model.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioLoginRequestDTO(@NotBlank
                                     String login,
                                     @NotBlank
                                     String senha) {
}
