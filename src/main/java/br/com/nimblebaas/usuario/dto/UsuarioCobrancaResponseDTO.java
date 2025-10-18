package br.com.nimblebaas.usuario.dto;

import br.com.nimblebaas.usuario.Usuario;

public record UsuarioCobrancaResponseDTO(Long id,
                                         String cpf,
                                         String nome) {
    public UsuarioCobrancaResponseDTO(Usuario usuario){
        this(usuario.getId(), usuario.getCpf(), usuario.getNome());
    }
}
