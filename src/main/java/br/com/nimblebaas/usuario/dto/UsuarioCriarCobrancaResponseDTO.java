package br.com.nimblebaas.usuario.dto;

import br.com.nimblebaas.usuario.Usuario;

public record UsuarioCriarCobrancaResponseDTO(Long id,
                                              String cpf,
                                              String nome) {
    public UsuarioCriarCobrancaResponseDTO(Usuario usuario){
        this(usuario.getId(), usuario.getCpf(), usuario.getNome());
    }
}
