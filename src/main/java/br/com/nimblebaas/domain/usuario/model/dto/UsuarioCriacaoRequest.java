package br.com.nimblebaas.domain.usuario.model.dto;

import br.com.nimblebaas.infraestrutura.validacao.cpf.Cpf;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record UsuarioCriacaoRequest(@NotBlank
                                    String nome,
                                    @NotBlank
                                    @Email
                                    String email,
                                    @Cpf
                                    String cpf,
                                    @NotBlank
                                    String senha) {
}
