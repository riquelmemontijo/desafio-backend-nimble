package br.com.nimblebaas.domain.usuario.model.dto;

import br.com.nimblebaas.infraestrutura.validacao.cpf.Cpf;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioCriacaoRequestDTO(@NotBlank
                                       String nome,
                                       @NotBlank
                                       @Email
                                       String email,
                                       @Cpf
                                       String cpf,
                                       @NotBlank
                                       String senha) {
}
