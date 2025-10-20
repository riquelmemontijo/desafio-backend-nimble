package br.com.nimblebaas.util.usuario;

import br.com.nimblebaas.infraestrutura.exception.RegistroNaoEncontradoException;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.domain.usuario.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioUtils {

    private final UsuarioRepository usuarioRepository;

    public UsuarioUtils(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario getUsuarioLogado(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
    }

    public Usuario buscarUsuarioPorCpf(String cpf){
        return usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Usuario não foi localizado no sistema"));
    }

}
