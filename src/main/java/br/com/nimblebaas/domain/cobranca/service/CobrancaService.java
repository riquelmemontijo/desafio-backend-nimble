package br.com.nimblebaas.domain.cobranca.service;

import br.com.nimblebaas.domain.cobranca.repository.CobrancaRepository;
import br.com.nimblebaas.domain.cobranca.repository.CobrancaSpecification;
import br.com.nimblebaas.domain.cobranca.model.cancelamentostrategy.SeletorDeEstrategiaDeCancelamento;
import br.com.nimblebaas.cobranca.dto.*;
import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.FormaDePagamento;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.TipoRelacaoCobranca;
import br.com.nimblebaas.domain.cobranca.model.dto.*;
import br.com.nimblebaas.domain.cobranca.validacao.criacao.ValidacaoCriacaoCobranca;
import br.com.nimblebaas.domain.cobranca.validacao.pagamento.cartao.ValidacaoPagamentoCobrancaCartao;
import br.com.nimblebaas.domain.cobranca.validacao.pagamento.geral.ValidacaoPagamentoCobranca;
import br.com.nimblebaas.domain.cobranca.validacao.pagamento.saldo.ValidacaoPagamentoCobrancaSaldo;
import br.com.nimblebaas.infraestrutura.exception.RegistroNaoEncontradoException;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import br.com.nimblebaas.servicos.autorizador.ClientAutorizador;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.domain.usuario.repository.UsuarioRepository;
import br.com.nimblebaas.util.usuario.UsuarioUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CobrancaService {

    private final CobrancaRepository cobrancaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClientAutorizador clientAutorizador;
    private final List<ValidacaoCriacaoCobranca> validacaoCriacaoCobranca;
    private final List<ValidacaoPagamentoCobranca> validacaoPagamentoCobranca;
    private final List<ValidacaoPagamentoCobrancaSaldo> validacaoPagamentoCobrancaSaldo;
    private final List<ValidacaoPagamentoCobrancaCartao> validacaoPagamentoCobrancaCartao;
    private final UsuarioUtils usuarioUtils;
    private final SeletorDeEstrategiaDeCancelamento seletorDeEstrategiaDeCancelamento;

    public CobrancaService(CobrancaRepository cobrancaRepository,
                           UsuarioRepository usuarioRepository,
                           ClientAutorizador clientAutorizador,
                           List<ValidacaoCriacaoCobranca> validacaoCriacaoCobranca,
                           List<ValidacaoPagamentoCobranca> validacaoPagamentoCobranca,
                           List<ValidacaoPagamentoCobrancaSaldo> validacaoPagamentoCobrancaSaldo,
                           List<ValidacaoPagamentoCobrancaCartao> validacaoPagamentoCobrancaCartao,
                           UsuarioUtils usuarioUtils, SeletorDeEstrategiaDeCancelamento seletorDeEstrategiaDeCancelamento) {
        this.cobrancaRepository = cobrancaRepository;
        this.usuarioRepository = usuarioRepository;
        this.clientAutorizador = clientAutorizador;
        this.validacaoCriacaoCobranca = validacaoCriacaoCobranca;
        this.validacaoPagamentoCobranca = validacaoPagamentoCobranca;
        this.validacaoPagamentoCobrancaSaldo = validacaoPagamentoCobrancaSaldo;
        this.validacaoPagamentoCobrancaCartao = validacaoPagamentoCobrancaCartao;
        this.usuarioUtils = usuarioUtils;
        this.seletorDeEstrategiaDeCancelamento = seletorDeEstrategiaDeCancelamento;
    }

    @Transactional
    public CriarCobrancaResponseDTO criarCobranca(CriarCobrancaRequestDTO cobranca){
        Cobranca cobrancaModel = configurarCobrancaParaCadastro(new Cobranca(cobranca));
        validacaoCriacaoCobranca.forEach(validacao -> validacao.validar(cobrancaModel));
        return new CriarCobrancaResponseDTO(cobrancaRepository.save(cobrancaModel));
    }

    public Page<CobrancaResponseDTO> obterCobrancas(Pageable pageable, StatusCobranca status, TipoRelacaoCobranca tipoRelacaoCobranca){
        var usuario = usuarioUtils.getUsuarioLogado();
        Page<Cobranca> cobrancas = cobrancaRepository.findAll(CobrancaSpecification.porStatusDaCobranca(status)
                .and(CobrancaSpecification.porRelacaoComACobranca(usuario, tipoRelacaoCobranca)), pageable);
        return cobrancas.map(CobrancaResponseDTO::new);
    }

    @Transactional
    public TransferenciaResponseDTO pagarCobrancaComSaldo(Long idCobranca){
        var cobranca = cobrancaRepository.findById(idCobranca)
                                         .orElseThrow(() -> new RegistroNaoEncontradoException("Cobranca não foi localizada no sistema"));
        validacaoPagamentoCobranca.forEach(validacao -> validacao.validar(cobranca));
        validacaoPagamentoCobrancaSaldo.forEach(validacao -> validacao.validar(cobranca));

        var credor = usuarioUtils.buscarUsuarioPorCpf(cobranca.getOriginador().getCpf());
        realizarTransferencia(credor, cobranca.getValor());

        cobranca.setStatusCobranca(StatusCobranca.PAGA);
        cobranca.setFormaDePagamento(FormaDePagamento.SALDO);
        cobrancaRepository.save(cobranca);

        var mensagem = "Transação realizada com sucesso. Cobrança paga!";
        return new TransferenciaResponseDTO(cobranca, credor, usuarioUtils.getUsuarioLogado(), mensagem);
    }

    @Transactional
    public TransferenciaResponseDTO pagarCobrancaComCartaoDeCredito(Long idCobranca, CartaoDeCreditoDTO cartao){
        var response = clientAutorizador.solicitarAutorizacao();
        if (!response.getData().isAuthorized()) {
            throw new RegraDeNegocioException("Transação não autorizada");
        }
        var cobranca = cobrancaRepository.findById(idCobranca)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Cobranca não foi localizada no sistema"));
        validacaoPagamentoCobranca.forEach(validacao -> validacao.validar(cobranca));
        validacaoPagamentoCobrancaCartao.forEach(validacao -> validacao.validar(cartao));
        cobranca.setStatusCobranca(StatusCobranca.PAGA);
        cobranca.setFormaDePagamento(FormaDePagamento.CARTAO_DE_CREDITO);
        cobrancaRepository.save(cobranca);
        var mensagem = "Transação realizada com sucesso. Cobrança paga!";
        return new TransferenciaResponseDTO(cobranca, cobranca.getOriginador(), cobranca.getDestinatario(), mensagem);
    }

    @Transactional
    public CancelamentoCobrancaDTO cancelarCobranca(Long idCobranca){
        var cobranca = cobrancaRepository.findById(idCobranca)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Cobranca não foi localizada no sistema"));

        seletorDeEstrategiaDeCancelamento.executarEstrategia(cobranca);

        var mensagem = "Cancelamento realizado com sucesso.";
        return new CancelamentoCobrancaDTO(cobranca, mensagem);
    }

    private Cobranca configurarCobrancaParaCadastro(Cobranca cobranca){
        cobranca.setOriginador(usuarioUtils.getUsuarioLogado());
        cobranca.setDestinatario(usuarioUtils.buscarUsuarioPorCpf(cobranca.getCpfDestinatario()));
        cobranca.setStatusCobranca(StatusCobranca.PENDENTE);
        return cobranca;
    }

    private void realizarTransferencia(Usuario credor, BigDecimal valor) {
        var devedor = usuarioUtils.getUsuarioLogado();
        devedor.debitarSaldo(valor);
        credor.creditarSaldo(valor);
        usuarioRepository.save(devedor);
        usuarioRepository.save(credor);
    }

    private void realizarEstorno(Cobranca cobranca){
        var devedor = cobranca.getDestinatario();
        var credor = cobranca.getOriginador();
        devedor.creditarSaldo(cobranca.getValor());
        credor.debitarSaldo(cobranca.getValor());
        usuarioRepository.save(devedor);
        usuarioRepository.save(credor);
    }

}
