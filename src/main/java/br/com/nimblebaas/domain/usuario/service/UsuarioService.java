package br.com.nimblebaas.domain.usuario.service;

import br.com.nimblebaas.domain.role.model.Role;
import br.com.nimblebaas.domain.role.repository.RoleRepository;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.domain.usuario.model.dto.*;
import br.com.nimblebaas.domain.usuario.repository.UsuarioRepository;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import br.com.nimblebaas.servicos.autorizador.AutorizadorResponse;
import br.com.nimblebaas.servicos.autorizador.ClientAutorizador;
import br.com.nimblebaas.util.usuario.UsuarioUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Service
public class UsuarioService {

    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientAutorizador clientAutorizador;
    private final UsuarioUtils usuarioUtils;

    public UsuarioService(JwtEncoder jwtEncoder, UsuarioRepository usuarioRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ClientAutorizador clientAutorizador, UsuarioUtils usuarioUtils) {
        this.jwtEncoder = jwtEncoder;
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientAutorizador = clientAutorizador;
        this.usuarioUtils = usuarioUtils;
    }

    @Transactional
    public UsuarioLoginResponseDTO signIn(UsuarioLoginRequestDTO usuarioLoginRequestDTO){
        var usuario = usuarioRepository.findByEmailOrCpf(usuarioLoginRequestDTO.login());
        if(usuario.isEmpty() || !loginEstaCorreto(usuarioLoginRequestDTO, usuario.get())){
            throw new BadCredentialsException("Credenciais inválidas");
        }
        var claims = criarClaims(usuario.get());
        var tokenJwt = gerarToken(claims);
        return new UsuarioLoginResponseDTO(tokenJwt, Duration.between(Instant.now(), claims.getExpiresAt()).toHours());
    }

    @Transactional
    public UsuarioCriacaoResponseDTO signUp(UsuarioCriacaoRequestDTO usuarioCriacaoRequestDTO){
        Usuario usuarioModel = configurarUsuarioParaCadastro(new Usuario(usuarioCriacaoRequestDTO));
        usuarioRepository.save(usuarioModel);
        return new UsuarioCriacaoResponseDTO(usuarioModel);
    }

    @Transactional
    public DepositoResponseDTO fazerDeposito(DepositoRequestDTO deposito){
        AutorizadorResponse response = clientAutorizador.solicitarAutorizacao();
        if(!response.getData().isAuthorized()){
            throw new RegraDeNegocioException("Depósito não autorizado");
        }
        var depositante = usuarioUtils.getUsuarioLogado();
        depositante.creditarSaldo(deposito.valor());
        var mensagem = "Depósito realizado com sucesso";
        return new DepositoResponseDTO(depositante.getNome(), depositante.getCpf(), deposito.valor(), mensagem);
    }

    private boolean loginEstaCorreto(UsuarioLoginRequestDTO usuarioLoginRequestDTO, Usuario usuario){
        var senhaInformada = usuarioLoginRequestDTO.senha();
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
