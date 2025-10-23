package br.com.nimblebaas.domain.usuario.model.dto;

import br.com.nimblebaas.domain.usuario.model.Usuario;

public record UsuarioCriacaoResponseDTO(Long id, String email, String cpf, String nome) {
    public UsuarioCriacaoResponseDTO(Usuario usuario){
        this(usuario.getId(), usuario.getEmail(), usuario.getCpf(), usuario.getNome());
    }
}
