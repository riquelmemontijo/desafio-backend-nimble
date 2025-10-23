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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ClientAutorizador clientAutorizador;
    @Mock
    private UsuarioUtils usuarioUtils;
    @Mock
    private Jwt jwt;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioLoginRequestDTO usuarioLoginRequestDTO;
    private UsuarioCriacaoRequestDTO usuarioCriacaoRequestDTO;
    private Role role;
    private AutorizadorResponse autorizacaoSuccessResponse;
    private AutorizadorResponse autorizacaoFailResponse;
    private DepositoResponseDTO depositoResponseDTO;
    private DepositoRequestDTO depositoRequestDTO;


    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(jwtEncoder, usuarioRepository, roleRepository, passwordEncoder, clientAutorizador, usuarioUtils);

        role = new Role(1L, "BASICO");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Nimble");
        usuario.setEmail("Nimble");
        usuario.setSenha("Nimble");
        usuario.setCpf("12345678910");
        usuario.setRoles(Set.of(role));
        usuario.creditarSaldo(new BigDecimal(100));


        usuarioLoginRequestDTO = new UsuarioLoginRequestDTO("Nimle", "Nimble");
        usuarioCriacaoRequestDTO = new UsuarioCriacaoRequestDTO("Nimble", "Nimble", "Nimmle", "Nimble");

        autorizacaoFailResponse = new AutorizadorResponse("fail", new AutorizadorResponse.Data(false));
        autorizacaoSuccessResponse = new AutorizadorResponse("success", new AutorizadorResponse.Data(true));
        depositoResponseDTO = new DepositoResponseDTO("Nimble", "12345678910", new BigDecimal(100), "Depósito realizado com sucesso");
        depositoRequestDTO = new DepositoRequestDTO(new BigDecimal(100));
    }

    @Test
    @DisplayName("Deve fazer login com sucesso quando as credenciais estiverem corretas")
    void deveFazerLoginComSucesso(){
        // arrange

        when(usuarioRepository.findByEmailOrCpf(usuarioLoginRequestDTO.login()))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches(usuario.getSenha(), usuarioLoginRequestDTO.senha()))
                .thenReturn(true);

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        when(jwt.getTokenValue())
                .thenReturn("token");

        // act
        var response = usuarioService.signIn(usuarioLoginRequestDTO);

        // assert
        assertNotNull(response);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuario nao existir")
    void deveLancarExcecaoQuandoUsuarioNaoExistir(){
        // arrange
        when(usuarioRepository.findByEmailOrCpf(usuarioLoginRequestDTO.login()))
                .thenReturn(Optional.empty());

        // act and assert
        assertThrows(BadCredentialsException.class, () -> usuarioService.signIn(usuarioLoginRequestDTO));
        verify(usuarioRepository).findByEmailOrCpf(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtEncoder, never()).encode(any(JwtEncoderParameters.class));
        verify(jwt, never()).getTokenValue();

    }

    @Test
    @DisplayName("Deve lançar exceção quando senha estiver incorreta")
    void deveLancarExcecaoQuandoSenhaEstiverIncorreta(){
        // arrange
        when(usuarioRepository.findByEmailOrCpf(usuarioLoginRequestDTO.login()))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(usuario.getSenha(), usuarioLoginRequestDTO.senha()))
                .thenReturn(false);


        // act and assert
        assertThrows(BadCredentialsException.class, () -> usuarioService.signIn(usuarioLoginRequestDTO));

        verify(usuarioRepository).findByEmailOrCpf(anyString());
        verify(jwtEncoder, never()).encode(any(JwtEncoderParameters.class));
        verify(jwt, never()).getTokenValue();

    }

    @Test
    @DisplayName("Deve cadastrar o usuário com sucesso")
    void deveCadastrarOUsuarioComSucesso(){
        // arrange
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocationOnMock -> {
                    Usuario usuarioSalvo = invocationOnMock.getArgument(0);
                    usuarioSalvo.setId(1L);
                    return usuarioSalvo;
                });
        when(passwordEncoder.encode(usuario.getSenha())).thenReturn("Nimble");
        when(roleRepository.findByDescricao(anyString())).thenReturn(role);

        // act
        UsuarioCriacaoResponseDTO usuarioCriacaoResponseDTO = usuarioService.signUp(usuarioCriacaoRequestDTO);

        // assert
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario usuarioSalvo = usuarioCaptor.getValue();

        assertNotNull(usuarioSalvo);
        assertEquals(usuarioCriacaoRequestDTO.nome(), usuarioSalvo.getNome());
        assertEquals(usuarioCriacaoRequestDTO.email(), usuarioSalvo.getEmail());
        assertEquals(usuarioCriacaoRequestDTO.cpf(), usuarioSalvo.getCpf());
        assertEquals(usuarioCriacaoRequestDTO.senha(), usuarioSalvo.getSenha());
        assertEquals(Set.of(role), usuarioSalvo.getRoles());

        assertNotNull(usuarioCriacaoResponseDTO);
        assertEquals(usuarioCriacaoRequestDTO.nome(), usuarioCriacaoResponseDTO.nome());
        assertEquals(usuarioCriacaoRequestDTO.email(), usuarioCriacaoResponseDTO.email());
        assertEquals(usuarioCriacaoRequestDTO.cpf(), usuarioCriacaoResponseDTO.cpf());
    }

    @Test
    @DisplayName("Deve fazer o depósito com sucesso")
    void deveFazerODepositoComSucesso(){
        // arrange
        when(clientAutorizador.solicitarAutorizacao())
                .thenReturn(autorizacaoSuccessResponse);
        when(usuarioUtils.getUsuarioLogado()).thenReturn(usuario);

        // act
        DepositoResponseDTO deposito = usuarioService.fazerDeposito(depositoRequestDTO);

        // assert
        assertNotNull(deposito);
        assertEquals(depositoResponseDTO.nomeUsuario(), deposito.nomeUsuario());
        assertEquals(depositoResponseDTO.cpf(), deposito.cpf());
        assertEquals(depositoResponseDTO.valor(), deposito.valor());
        assertEquals(depositoResponseDTO.mensagem(), deposito.mensagem());
        assertEquals(new BigDecimal("200"), usuario.getSaldo());

    }

    @Test
    @DisplayName("Deve lançar exceção se o depósito não for autorizado")
    void deveLancarExcecaoSeODepositoNaoForAutorizado(){
        // arrange
        when(clientAutorizador.solicitarAutorizacao())
                .thenReturn(autorizacaoFailResponse);

        // act and assert
        assertThrows(RegraDeNegocioException.class,
                () -> usuarioService.fazerDeposito(depositoRequestDTO));

        verify(clientAutorizador).solicitarAutorizacao();
        verify(usuarioUtils, never()).getUsuarioLogado();
        verify(usuarioRepository, never()).save(any(Usuario.class));

    }

}