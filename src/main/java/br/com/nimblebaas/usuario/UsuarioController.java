package br.com.nimblebaas.usuario;

import br.com.nimblebaas.usuario.dto.UsuarioLoginRequest;
import br.com.nimblebaas.usuario.dto.UsuarioLoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(JwtEncoder jwtEncoder, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioLoginResponse> login(@RequestBody UsuarioLoginRequest loginRequest) {
        var usuario = usuarioRepository.findByEmail(loginRequest.login());
        if(usuario.isEmpty() || !loginEstaCorreto(loginRequest.senha(), usuario.get().getSenha())){
            throw new BadCredentialsException("Usuario ou senha invalidos");
        }
        var now = Instant.now();
        var expiresIn = 300L;
        var claims = JwtClaimsSet.builder()
                .issuer("nimble baas")
                .subject(usuario.get().getEmail())
                .claim("nome", usuario.get().getNome())
                .expiresAt(now.plusSeconds(expiresIn))
                .issuedAt(Instant.now())
                .build();
        var tokenJwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(new UsuarioLoginResponse(tokenJwt, expiresIn));
    }

    private boolean loginEstaCorreto(String senhaInformada, String senhaCriptografada){
        return passwordEncoder.matches(senhaInformada, senhaCriptografada);
    }

}
