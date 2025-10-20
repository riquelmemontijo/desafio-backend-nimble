package br.com.nimblebaas.domain.cobranca.service;

import br.com.nimblebaas.domain.cobranca.model.FormaDePagamento;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.dto.CartaoDeCreditoDTO;
import br.com.nimblebaas.domain.cobranca.model.dto.TransferenciaResponseDTO;
import br.com.nimblebaas.domain.cobranca.repository.CobrancaRepository;
import br.com.nimblebaas.domain.cobranca.validacao.pagamento.cartao.ValidacaoPagamentoCobrancaCartao;
import br.com.nimblebaas.domain.cobranca.validacao.pagamento.geral.ValidacaoPagamentoCobranca;
import br.com.nimblebaas.domain.cobranca.validacao.pagamento.saldo.ValidacaoPagamentoCobrancaSaldo;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.domain.usuario.repository.UsuarioRepository;
import br.com.nimblebaas.infraestrutura.exception.RegistroNaoEncontradoException;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import br.com.nimblebaas.servicos.autorizador.ClientAutorizador;
import br.com.nimblebaas.util.usuario.UsuarioUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PagarCobrancaService {

    private final CobrancaRepository cobrancaRepository;
    private final UsuarioRepository usuarioRepository;
    private final List<ValidacaoPagamentoCobranca> validacaoPagamentoCobranca;
    private final List<ValidacaoPagamentoCobrancaSaldo> validacaoPagamentoCobrancaSaldo;
    private final List<ValidacaoPagamentoCobrancaCartao> validacaoPagamentoCobrancaCartao;
    private final UsuarioUtils usuarioUtils;
    private final ClientAutorizador clientAutorizador;

    public PagarCobrancaService(CobrancaRepository cobrancaRepository, UsuarioRepository usuarioRepository, List<ValidacaoPagamentoCobranca> validacaoPagamentoCobranca, List<ValidacaoPagamentoCobrancaSaldo> validacaoPagamentoCobrancaSaldo, List<ValidacaoPagamentoCobrancaCartao> validacaoPagamentoCobrancaCartao, UsuarioUtils usuarioUtils, ClientAutorizador clientAutorizador) {
        this.cobrancaRepository = cobrancaRepository;
        this.usuarioRepository = usuarioRepository;
        this.validacaoPagamentoCobranca = validacaoPagamentoCobranca;
        this.validacaoPagamentoCobrancaSaldo = validacaoPagamentoCobrancaSaldo;
        this.validacaoPagamentoCobrancaCartao = validacaoPagamentoCobrancaCartao;
        this.usuarioUtils = usuarioUtils;
        this.clientAutorizador = clientAutorizador;
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

    private void realizarTransferencia(Usuario credor, BigDecimal valor) {
        var devedor = usuarioUtils.getUsuarioLogado();
        devedor.debitarSaldo(valor);
        credor.creditarSaldo(valor);
        usuarioRepository.save(devedor);
        usuarioRepository.save(credor);
    }

}
