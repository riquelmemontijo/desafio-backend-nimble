package br.com.nimblebaas.domain.usuario.model.dto;

import br.com.nimblebaas.domain.usuario.model.Usuario;

public record UsuarioCobrancaResponseDTO(Long id,
                                         String cpf,
                                         String nome) {
    public UsuarioCobrancaResponseDTO(Usuario usuario){
        this(usuario.getId(), usuario.getCpf(), usuario.getNome());
    }
}
