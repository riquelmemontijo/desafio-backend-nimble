package br.com.nimblebaas.usuario;

import br.com.nimblebaas.usuario.dto.UsuarioCriacaoRequest;
import br.com.nimblebaas.usuario.dto.UsuarioCriacaoResponse;
import br.com.nimblebaas.usuario.dto.UsuarioLoginRequest;
import br.com.nimblebaas.usuario.dto.UsuarioLoginResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Service
public class UsuarioService {

    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(JwtEncoder jwtEncoder, UsuarioRepository usuarioRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioLoginResponse signIn(UsuarioLoginRequest usuarioLoginRequest){
        var usuario = usuarioRepository.findByEmailOrCpf(usuarioLoginRequest.login());
        if(usuario.isEmpty() || !loginEstaCorreto(usuarioLoginRequest, usuario.get())){
            throw new BadCredentialsException("Credenciais inválidas");
        }
        var claims = criarClaims(usuario.get());
        var tokenJwt = gerarToken(claims);
        return new UsuarioLoginResponse(tokenJwt, Duration.between(Instant.now(), claims.getExpiresAt()).toHours());
    }

    public UsuarioCriacaoResponse signUp(UsuarioCriacaoRequest usuarioCriacaoRequest){
        Usuario usuarioModel = configurarUsuarioParaCadastro(new Usuario(usuarioCriacaoRequest));
        usuarioRepository.save(usuarioModel);
        return new UsuarioCriacaoResponse(usuarioModel);
    }

    private boolean loginEstaCorreto(UsuarioLoginRequest usuarioLoginRequest, Usuario usuario){
        var senhaInformada = usuarioLoginRequest.senha();
        var senhaCriptografada = usuario.getSenha();
        return passwordEncoder.matches(senhaInformada, senhaCriptografada);
    }

    private JwtClaimsSet criarClaims(Usuario usuario){
        var now = Instant.now();
        long expiresIn = 30000L;
        return JwtClaimsSet.builder()
                .issuer("nimble baas")
                .subject(usuario.getEmail())
                .claim("nome", usuario.getNome())
                .expiresAt(now.plusSeconds(expiresIn))
                .issuedAt(Instant.now())
                .build();
    }

    private String gerarToken(JwtClaimsSet claims){
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private Usuario configurarUsuarioParaCadastro(Usuario usuario){
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        Role roleBasico = roleRepository.findByDescricao(Role.RoleValues.BASICO.name());
        usuario.setSenha(senhaCriptografada);
        usuario.setRoles(Set.of(roleBasico));
        return usuario;
    }

}
