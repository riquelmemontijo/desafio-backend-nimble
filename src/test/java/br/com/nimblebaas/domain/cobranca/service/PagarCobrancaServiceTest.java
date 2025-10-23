package br.com.nimblebaas.domain.cobranca.service;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.FormaDePagamento;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.dto.CartaoDeCreditoRequestDTO;
import br.com.nimblebaas.domain.cobranca.model.dto.TransferenciaResponseDTO;
import br.com.nimblebaas.domain.cobranca.repository.CobrancaRepository;
import br.com.nimblebaas.domain.cobranca.validacao.pagamento.cartao.ValidacaoPagamentoCobrancaCartao;
import br.com.nimblebaas.domain.cobranca.validacao.pagamento.geral.ValidacaoPagamentoCobranca;
import br.com.nimblebaas.domain.cobranca.validacao.pagamento.saldo.ValidacaoPagamentoCobrancaSaldo;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.domain.usuario.repository.UsuarioRepository;
import br.com.nimblebaas.infraestrutura.exception.RegistroNaoEncontradoException;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagarCobrancaServiceTest {

    @InjectMocks
    private PagarCobrancaService service;

    @Mock
    private CobrancaRepository cobrancaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ValidacaoPagamentoCobranca validacaoPagamentoCobranca;
    @Mock
    private ValidacaoPagamentoCobrancaSaldo validacaoPagamentoCobrancaSaldo;
    @Mock
    private ValidacaoPagamentoCobrancaCartao validacaoPagamentoCobrancaCartao;
    @Mock
    private UsuarioUtils usuarioUtils;
    @Mock
    private ClientAutorizador clientAutorizador;

    @Captor
    private ArgumentCaptor<Cobranca> cobrancaCaptor;
    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    private Long idCobranca;
    private CartaoDeCreditoRequestDTO cartaoDeCreditoRequestDTO;
    private Usuario originador;
    private Usuario destinatario;
    private Cobranca cobranca;
    private AutorizadorResponse autorizacaoSuccessResponse;
    private AutorizadorResponse autorizacaoFailResponse;


    @BeforeEach
    void setUp() {
        idCobranca = 1L;

        service = new PagarCobrancaService(
                cobrancaRepository,
                usuarioRepository,
                List.of(validacaoPagamentoCobranca),
                List.of(validacaoPagamentoCobrancaSaldo),
                List.of(validacaoPagamentoCobrancaCartao),
                usuarioUtils,
                clientAutorizador
        );

        originador = new Usuario();
        originador.setId(1L);
        originador.setCpf("11111111111");
        originador.creditarSaldo(new BigDecimal("1000.00"));

        destinatario = new Usuario();
        destinatario.setId(2L);
        destinatario.setCpf("22222222222");
        destinatario.creditarSaldo(new BigDecimal("500.00"));

        cobranca = new Cobranca();
        cobranca.setId(1L);
        cobranca.setOriginador(originador);
        cobranca.setDestinatario(destinatario);
        cobranca.setValor(new BigDecimal("100.00"));

        cartaoDeCreditoRequestDTO = new CartaoDeCreditoRequestDTO("12345678910121416","03/29","123");

        autorizacaoSuccessResponse = new AutorizadorResponse("success", new AutorizadorResponse.Data(true));
        autorizacaoFailResponse = new AutorizadorResponse("fail", new AutorizadorResponse.Data(false));

    }

    @Test
    @DisplayName("pagarCobrancaComSaldo | Deve lançar exceção quando não existir cobrança ao pagar com saldo")
    void deveLancarExcecaoQuandoNaoExisteCobrancaAoPagarComSaldo(){
        // arrange
        when(cobrancaRepository.findById(idCobranca)).thenReturn(Optional.empty());

        // act e assert
        assertThrows(RegistroNaoEncontradoException.class,
                () -> service.pagarCobrancaComSaldo(idCobranca));

        verify(cobrancaRepository, times(1)).findById(idCobranca);
        verify(validacaoPagamentoCobranca, never()).validar(cobranca);
        verify(validacaoPagamentoCobrancaSaldo, never()).validar(cobranca);
        verify(usuarioUtils, never()).buscarUsuarioPorCpf(anyString());
        verifyNoMoreInteractions(usuarioRepository, usuarioUtils);
    }

    @Test
    @DisplayName("pagarCobrancaComSaldo | Deve pagar com sucesso e atualizar saldos e status da cobrança")
    void devePagarCobrancaComSucessoComSaldo() {
        // arrange
        cobranca.setStatusCobranca(StatusCobranca.PENDENTE);
        when(cobrancaRepository.findById(idCobranca)).thenReturn(Optional.of(cobranca));
        when(usuarioUtils.buscarUsuarioPorCpf(originador.getCpf())).thenReturn(originador);
        when(usuarioUtils.getUsuarioLogado()).thenReturn(destinatario);

        // act
        TransferenciaResponseDTO response = service.pagarCobrancaComSaldo(idCobranca);

        // assert
        assertNotNull(response);
        assertEquals("Transação realizada com sucesso. Cobrança paga!", response.mensagem());

        verify(usuarioRepository, times(2)).save(usuarioCaptor.capture());
        List<Usuario> savedUsers = usuarioCaptor.getAllValues();
        assertEquals(new BigDecimal("400.00"), savedUsers.get(0).getSaldo());
        assertEquals(new BigDecimal("1100.00"), savedUsers.get(1).getSaldo());

        verify(cobrancaRepository).save(cobrancaCaptor.capture());
        Cobranca savedCobranca = cobrancaCaptor.getValue();
        assertEquals(StatusCobranca.PAGA, savedCobranca.getStatusCobranca());
        assertEquals(FormaDePagamento.SALDO, savedCobranca.getFormaDePagamento());

        verify(validacaoPagamentoCobranca).validar(cobranca);
        verify(validacaoPagamentoCobrancaSaldo).validar(cobranca);
        verify(usuarioUtils).buscarUsuarioPorCpf(originador.getCpf());
        verify(usuarioUtils, times(2)).getUsuarioLogado();
        verify(usuarioRepository, times(2)).save(usuarioCaptor.capture());
        verify(cobrancaRepository).save(cobrancaCaptor.capture());

    }

    @Test
    @DisplayName("pagarCobrancaComSaldo | Deve lançar excecao se validacao geral de pagamento falhar")
    void deveLancarExcecaoSeValidacaoGeralDePagamentoFalharAoPagarComSaldo(){
        // arrange
        cobranca.setStatusCobranca(StatusCobranca.PAGA);
        when(cobrancaRepository.findById(idCobranca)).thenReturn(Optional.of(cobranca));

        // act
        doThrow(new RegraDeNegocioException(""))
                .when(validacaoPagamentoCobranca)
                .validar(cobranca);

        // assert
        assertThrows(RegraDeNegocioException.class, () -> service.pagarCobrancaComSaldo(idCobranca));

        verify(cobrancaRepository).findById(idCobranca);
        verify(validacaoPagamentoCobranca).validar(cobranca);
        verify(validacaoPagamentoCobrancaSaldo, never()).validar(cobranca);
        verify(usuarioUtils, never()).buscarUsuarioPorCpf(anyString());
        verifyNoMoreInteractions(usuarioRepository, usuarioUtils);
    }

    @Test
    @DisplayName("pagarCobrancaComCartaoDeCredito | Deve lançar exceção quando não existi cobrancaça ao pagar com cartão de crédito")
    void deveLancarExcecaoQuandoNaoExisteCobrancaAoPagarComCartao(){
        // arrange
        when(cobrancaRepository.findById(idCobranca)).thenReturn(Optional.empty());
        when(clientAutorizador.solicitarAutorizacao()).thenReturn(autorizacaoSuccessResponse);

        // act e assert
        assertThrows(RegistroNaoEncontradoException.class,
                () -> service.pagarCobrancaComCartaoDeCredito(idCobranca, cartaoDeCreditoRequestDTO));

        verify(cobrancaRepository).findById(idCobranca);
        verify(validacaoPagamentoCobranca, never()).validar(cobranca);
        verify(validacaoPagamentoCobrancaCartao, never()).validar(cartaoDeCreditoRequestDTO);
        verify(usuarioUtils, never()).buscarUsuarioPorCpf(anyString());
        verifyNoMoreInteractions(usuarioRepository, usuarioUtils);
    }

    @Test
    @DisplayName("pagarCobrancaComCartaoDeCredito | Deve lancar excecao quando pagamento com cartão não for autorizado")
    void deveLancarExcecaoQuandoPagamentoComCartaoNaoForAutorizado(){
        // arrange
        when(clientAutorizador.solicitarAutorizacao()).thenReturn(autorizacaoFailResponse);

        // act e assert
        assertThrows(RegraDeNegocioException.class,
                () -> service.pagarCobrancaComCartaoDeCredito(idCobranca, cartaoDeCreditoRequestDTO));

        verify(clientAutorizador).solicitarAutorizacao();
        verify(cobrancaRepository, never()).findById(idCobranca);
        verify(validacaoPagamentoCobranca, never()).validar(cobranca);
        verify(validacaoPagamentoCobrancaCartao, never()).validar(cartaoDeCreditoRequestDTO);
        verify(usuarioUtils, never()).buscarUsuarioPorCpf(anyString());
        verifyNoMoreInteractions(usuarioRepository, usuarioUtils);
    }

    @Test
    @DisplayName("pagarCobrancaComCartaoDeCredito | Deve pagar com sucesso e atualizar saldos e status da cobrança")
    void devePagarCobrancaComSucessoComCartaoDeCredito(){
        // arrange
        when(clientAutorizador.solicitarAutorizacao()).thenReturn(autorizacaoSuccessResponse);
        when(cobrancaRepository.findById(idCobranca)).thenReturn(Optional.of(cobranca));

        // act
        TransferenciaResponseDTO response = service.pagarCobrancaComCartaoDeCredito(idCobranca, cartaoDeCreditoRequestDTO);

        // assert
        assertNotNull(response);
        assertEquals("Transação realizada com sucesso. Cobrança paga!", response.mensagem());

        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertEquals(new BigDecimal("1100.00"), usuarioCaptor.getValue().getSaldo());

        verify(cobrancaRepository).save(cobrancaCaptor.capture());
        Cobranca savedCobranca = cobrancaCaptor.getValue();
        assertEquals(StatusCobranca.PAGA, savedCobranca.getStatusCobranca());
        assertEquals(FormaDePagamento.CARTAO_DE_CREDITO, savedCobranca.getFormaDePagamento());
    }

    @Test
    @DisplayName("pagamentoComCartaoDeCredito | Deve lançar excecao se validacao geral de pagamento falhar")
    void deveLancarExcecaoSeValidacaoGeralDePagamentoFalharAoPagarComCartaoDeCredito(){
        // arrange
        when(cobrancaRepository.findById(idCobranca)).thenReturn(Optional.of(cobranca));
        when(clientAutorizador.solicitarAutorizacao()).thenReturn(autorizacaoSuccessResponse);

        // act
        doThrow(new RegraDeNegocioException(""))
                .when(validacaoPagamentoCobranca)
                .validar(cobranca);

        // assert
        assertThrows(RegraDeNegocioException.class,
                () -> service.pagarCobrancaComCartaoDeCredito(idCobranca, cartaoDeCreditoRequestDTO));

        verify(cobrancaRepository).findById(idCobranca);
        verify(validacaoPagamentoCobranca).validar(cobranca);
        verify(validacaoPagamentoCobrancaSaldo, never()).validar(cobranca);
        verify(usuarioUtils, never()).buscarUsuarioPorCpf(anyString());
        verifyNoMoreInteractions(usuarioRepository, usuarioUtils);
    }
}