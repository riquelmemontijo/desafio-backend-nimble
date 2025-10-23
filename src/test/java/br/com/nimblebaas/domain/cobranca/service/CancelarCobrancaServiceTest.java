package br.com.nimblebaas.domain.cobranca.service;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.FormaDePagamento;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.cancelamentostrategy.SeletorDeEstrategiaDeCancelamento;
import br.com.nimblebaas.domain.cobranca.model.dto.CancelamentoCobrancaResponseDTO;
import br.com.nimblebaas.domain.cobranca.repository.CobrancaRepository;
import br.com.nimblebaas.domain.cobranca.validacao.cancelamento.ValidacaoCancelamentoCobranca;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.infraestrutura.exception.RegistroNaoEncontradoException;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
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
class CancelarCobrancaServiceTest {



    @Mock
    private CobrancaRepository cobrancaRepository;

    @Mock
    private SeletorDeEstrategiaDeCancelamento seletorDeEstrategiaDeCancelamento;

    @Mock
    private List<ValidacaoCancelamentoCobranca> validacoes;

    @InjectMocks
    private CancelarCobrancaService cancelarCobrancaService;

    @Captor
    private ArgumentCaptor<Cobranca> cobrancaCaptor;

    private Cobranca cobranca;
    private final Long cobrancaId = 1L;
    private Usuario credor;
    private Usuario devedor;


    @BeforeEach
    void setUp() {

        cancelarCobrancaService = new CancelarCobrancaService(
                cobrancaRepository,
                seletorDeEstrategiaDeCancelamento,
                validacoes
        );

        Usuario originador = new Usuario();
        originador.setNome("Credor");

        Usuario destinatario = new Usuario();
        destinatario.setNome("Devedor");

        cobranca = new Cobranca();
        cobranca.setId(cobrancaId);
        cobranca.setStatusCobranca(StatusCobranca.PENDENTE);
        cobranca.setFormaDePagamento(FormaDePagamento.SALDO);
        cobranca.setOriginador(originador);
        cobranca.setDestinatario(destinatario);
        cobranca.setValor(new BigDecimal("100.00"));

        credor = new Usuario();
        credor.setId(1L);
        credor.creditarSaldo(new BigDecimal("200.00"));


        devedor = new Usuario();
        devedor.setId(2L);
        devedor.creditarSaldo(new BigDecimal("100.00"));

        cobranca.setOriginador(credor);
        cobranca.setDestinatario(devedor);

    }

    @Test
    @DisplayName("Deve lançar exceção quando cobrança não existir")
    void deveLancarExcecaoQuandoCobrancaNaoExistente() {
        // arrange
        when(cobrancaRepository.findById(cobrancaId)).thenReturn(Optional.empty());

        // act e assert
        assertThrows(RegistroNaoEncontradoException.class,
                () -> cancelarCobrancaService.cancelarCobranca(cobrancaId));
        verify(validacoes, never()).forEach(any());
        verify(seletorDeEstrategiaDeCancelamento, never()).executarEstrategia(any());

    }

    @Test
    @DisplayName("Deve enviar cobrancas com status válidos para seletor de estratégias")
    void deveEnviarCobrancasComStatusValidosParaSeletorDeEstrategias() {
        // arrange
        when(cobrancaRepository.findById(cobrancaId)).thenReturn(Optional.of(cobranca));

        // act
        cancelarCobrancaService.cancelarCobranca(cobrancaId);

        // assert
        verify(validacoes).forEach(any());
        verify(seletorDeEstrategiaDeCancelamento).executarEstrategia(cobrancaCaptor.capture());
        seletorDeEstrategiaDeCancelamento.executarEstrategia(cobrancaCaptor.capture());
        assertNotEquals(StatusCobranca.CANCELADA, cobrancaCaptor.getValue().getStatusCobranca());
        assertNotEquals(null, cobrancaCaptor.getValue().getStatusCobranca());
        assertNotEquals(null, cobrancaCaptor.getValue().getFormaDePagamento());
        verify(validacoes).forEach(any());
        verify(seletorDeEstrategiaDeCancelamento).executarEstrategia(cobranca);
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação de cancelamento falhar")
    void deveLancarExcecaoQuandoValidacaoDeCancelamentoFalhar() {
        // arrange
        when(cobrancaRepository.findById(cobrancaId)).thenReturn(Optional.of(cobranca));
        doThrow(new RegraDeNegocioException("Erro de validação"))
                .when(validacoes).forEach(any());

        // act e assert
        assertThrows(RegraDeNegocioException.class,
                () -> cancelarCobrancaService.cancelarCobranca(cobrancaId));
        verify(validacoes).forEach(any());
        verify(seletorDeEstrategiaDeCancelamento, never()).executarEstrategia(any());

    }

    @Test
    @DisplayName("Deve cancelar cobrança com sucesso")
    void deveCancelarCobrancaComSucesso(){
        //arrange
        when(cobrancaRepository.findById(cobrancaId)).thenReturn(Optional.of(cobranca));

        //act
        CancelamentoCobrancaResponseDTO resultado = cancelarCobrancaService.cancelarCobranca(cobrancaId);

        //assert
        assertNotNull(resultado);
        assertEquals("Cancelamento realizado com sucesso.", resultado.mensagem());
        assertEquals(cobrancaId, resultado.idCobranca());


        verify(validacoes).forEach(any());
        verify(seletorDeEstrategiaDeCancelamento).executarEstrategia(cobranca);

    }

    @Test
    @DisplayName("Deve debitar do credor e restituir destinatario quando metodo de pagamento foi saldo")
    void deveDebitarDoCredorERestituirDestinatarioQuandoMetodoDePagamentoFoiSaldo(){
        //arrange
        when(cobrancaRepository.findById(cobrancaId)).thenReturn(Optional.of(cobranca));

        //act
        cancelarCobrancaService.cancelarCobranca(cobrancaId);

        //assert
        assertNotEquals(new BigDecimal("100.00"), credor.getSaldo());
        assertNotEquals(new BigDecimal("200.00"), devedor.getSaldo());

    }

    @Test
    @DisplayName("Deve debitar do credor quando metodo de pagamento foi cartão")
    void deveDebitarDoCredorQuandoMetodoDePagamentoFoiCartao(){
        //arrange
        cobranca.setFormaDePagamento(FormaDePagamento.CARTAO_DE_CREDITO);
        when(cobrancaRepository.findById(cobrancaId)).thenReturn(Optional.of(cobranca));

        //act
        cancelarCobrancaService.cancelarCobranca(cobrancaId);

        //assert
        assertNotEquals(new BigDecimal("0.00"), credor.getSaldo());

    }

}