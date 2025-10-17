package br.com.nimblebaas.usuario.dto;

import br.com.nimblebaas.usuario.Usuario;

public record UsuarioCriacaoResponse(Long id, String email, String cpf, String nome) {
    public UsuarioCriacaoResponse(Usuario usuario){
        this(usuario.getId(), usuario.getEmail(), usuario.getCpf(), usuario.getNome());
    }
}
