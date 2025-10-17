package br.com.nimblebaas.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record UsuarioCriacaoRequest(@NotBlank
                                    String nome,
                                    @NotBlank
                                    @Email
                                    String email,
                                    @NotBlank
                                    String cpf,
                                    @NotBlank
                                    String senha) {
}
