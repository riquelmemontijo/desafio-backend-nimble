package br.com.nimblebaas.usuario;

import br.com.nimblebaas.usuario.dto.UsuarioCriacaoRequest;
import br.com.nimblebaas.usuario.dto.UsuarioCriacaoResponse;
import br.com.nimblebaas.usuario.dto.UsuarioLoginRequest;
import br.com.nimblebaas.usuario.dto.UsuarioLoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioRepository usuarioRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UsuarioCriacaoResponse> signUp(@RequestBody @Valid UsuarioCriacaoRequest usuarioCriacaoRequest){
        return ResponseEntity.ok(usuarioService.signUp(usuarioCriacaoRequest));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<UsuarioLoginResponse> signIn(@RequestBody UsuarioLoginRequest usuarioLoginRequest) {
        return ResponseEntity.ok(usuarioService.signIn(usuarioLoginRequest));
    }

}
