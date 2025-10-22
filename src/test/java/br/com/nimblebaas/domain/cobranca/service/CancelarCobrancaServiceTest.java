package br.com.nimblebaas.domain.cobranca.service;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.FormaDePagamento;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.cancelamentostrategy.SeletorDeEstrategiaDeCancelamento;
import br.com.nimblebaas.domain.cobranca.model.dto.CancelamentoCobrancaDTO;
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
        CancelamentoCobrancaDTO resultado = cancelarCobrancaService.cancelarCobranca(cobrancaId);

        //assert
        assertNotNull(resultado);
        assertEquals("Cancelamento realizado com sucesso.", resultado.mensagem());
        assertEquals(cobrancaId, resultado.idCobranca());


        verify(validacoes).forEach(any());
        verify(seletorDeEstrategiaDeCancelamento).executarEstrategia(cobranca);

    }
}