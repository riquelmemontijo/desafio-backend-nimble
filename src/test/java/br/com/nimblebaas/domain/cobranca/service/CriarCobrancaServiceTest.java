package br.com.nimblebaas.domain.cobranca.service;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.dto.CriarCobrancaRequestDTO;
import br.com.nimblebaas.domain.cobranca.model.dto.CriarCobrancaResponseDTO;
import br.com.nimblebaas.domain.cobranca.repository.CobrancaRepository;
import br.com.nimblebaas.domain.cobranca.validacao.criacao.ValidacaoCriacaoCobranca;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import br.com.nimblebaas.infraestrutura.exception.RegistroNaoEncontradoException;
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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarCobrancaServiceTest {

    @Mock
    private CobrancaRepository cobrancaRepository;
    @Mock
    private ValidacaoCriacaoCobranca validador;
    @Mock
    private UsuarioUtils usuarioUtils;

    @InjectMocks
    private CriarCobrancaService criarCobrancaService;

    @Captor
    private ArgumentCaptor<Cobranca> cobrancaCaptor;

    private Usuario originador;
    private Usuario destinatario;
    private CriarCobrancaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        criarCobrancaService = new CriarCobrancaService(
                cobrancaRepository,
                List.of(validador),
                usuarioUtils
        );

        originador = new Usuario();
        originador.setId(1L);
        originador.setNome("Usuário Logado");

        destinatario = new Usuario();
        destinatario.setId(2L);
        destinatario.setNome("Usuário Destinatário");

        requestDTO = new CriarCobrancaRequestDTO(
                "12345678900",
                new BigDecimal("100.50"),
                "Cobrança de teste"
        );
    }

    @Test
    @DisplayName("Deve criar cobrança com sucesso")
    void deveCriarCobrancaComSucesso() {
        // arrange
        when(usuarioUtils.getUsuarioLogado()).thenReturn(originador);
        when(usuarioUtils.buscarUsuarioPorCpf(requestDTO.cpfDestinatario())).thenReturn(destinatario);
        doNothing().when(validador).validar(any(Cobranca.class));
        when(cobrancaRepository.save(any())).thenAnswer(invocation -> {
            Cobranca cobrancaSalva = invocation.getArgument(0);
            cobrancaSalva.setId(100L); // Simulate ID generation
            return cobrancaSalva;
        });

        // act
        CriarCobrancaResponseDTO response = criarCobrancaService.criarCobranca(requestDTO);

        // assert
        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(StatusCobranca.PENDENTE.toString(), response.status());

        verify(cobrancaRepository).save(cobrancaCaptor.capture());
        Cobranca cobrancaSalva = cobrancaCaptor.getValue();

        assertEquals(originador, cobrancaSalva.getOriginador());
        assertEquals(destinatario, cobrancaSalva.getDestinatario());
        assertEquals(StatusCobranca.PENDENTE, cobrancaSalva.getStatusCobranca());
        assertEquals(requestDTO.valor(), cobrancaSalva.getValor());
        assertEquals(requestDTO.descricao(), cobrancaSalva.getDescricao());
        assertEquals(requestDTO.cpfDestinatario(), cobrancaSalva.getCpfDestinatario());

        verify(validador).validar(cobrancaSalva);
    }

    @Test
    @DisplayName("Deve lançar exceção de regra de negócio quando validação falhar")
    void deveLancarExcecaoQuandoValidacaoFalhar() {
        // arrange
        when(usuarioUtils.getUsuarioLogado()).thenReturn(originador);
        when(usuarioUtils.buscarUsuarioPorCpf(requestDTO.cpfDestinatario())).thenReturn(destinatario);
        doThrow(new RegraDeNegocioException("Validação falhou"))
                .when(validador).validar(any());

        // act and assert
        assertThrows(RegraDeNegocioException.class, () -> criarCobrancaService.criarCobranca(requestDTO));

        verify(cobrancaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando destinatário não for encontrado")
    void deveLancarExcecaoQuandoDestinatarioNaoEncontrado() {
        // arrange
        when(usuarioUtils.getUsuarioLogado()).thenReturn(originador);
        when(usuarioUtils.buscarUsuarioPorCpf(requestDTO.cpfDestinatario()))
                .thenThrow(new RegistroNaoEncontradoException("Usuário destinatário não encontrado"));

        // act and assert
        assertThrows(RegistroNaoEncontradoException.class, () -> criarCobrancaService.criarCobranca(requestDTO));

        verify(validador, never()).validar(any());
        verify(cobrancaRepository, never()).save(any());
    }
}