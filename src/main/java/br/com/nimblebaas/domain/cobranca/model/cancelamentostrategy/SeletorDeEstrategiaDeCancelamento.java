package br.com.nimblebaas.domain.cobranca.model.cancelamentostrategy;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.repository.CobrancaRepository;
import br.com.nimblebaas.domain.cobranca.model.FormaDePagamento;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import br.com.nimblebaas.servicos.autorizador.ClientAutorizador;
import br.com.nimblebaas.domain.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
public class SeletorDeEstrategiaDeCancelamento {

    private final CobrancaRepository cobrancaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClientAutorizador clientAutorizador;

    private final Map<Predicate<Cobranca>, Consumer<Cobranca>> estrategias = new HashMap<>();

    public SeletorDeEstrategiaDeCancelamento(CobrancaRepository cobrancaRepository, UsuarioRepository usuarioRepository, ClientAutorizador clientAutorizador) {
        this.cobrancaRepository = cobrancaRepository;
        this.usuarioRepository = usuarioRepository;
        this.clientAutorizador = clientAutorizador;

        estrategias.put(
                cobranca -> cobranca.getStatusCobranca() == StatusCobranca.PENDENTE,
                this::cobrancaPendente
        );

        estrategias.put(
                cobranca -> cobranca.getStatusCobranca() == StatusCobranca.PAGA &&
                        cobranca.getFormaDePagamento() == FormaDePagamento.SALDO,
                this::cobrancaPagaComSaldo
        );

        estrategias.put(
                cobranca -> cobranca.getStatusCobranca() == StatusCobranca.PAGA &&
                        cobranca.getFormaDePagamento() == FormaDePagamento.CARTAO_DE_CREDITO,
                this::cobrancaPagaComCartao
        );

    }

    public void executarEstrategia(Cobranca cobranca){
        estrategias.entrySet().stream()
                .filter(estrategia -> estrategia.getKey().test(cobranca))
                .findFirst()
                .ifPresent(estrategia -> estrategia.getValue().accept(cobranca));
    }

    private void cobrancaPendente(Cobranca cobranca) {
        cobranca.setStatusCobranca(StatusCobranca.CANCELADA);
        cobrancaRepository.save(cobranca);
    }

    private void cobrancaPagaComSaldo(Cobranca cobranca) {
        var devedor = cobranca.getDestinatario();
        var credor = cobranca.getOriginador();
        devedor.creditarSaldo(cobranca.getValor());
        credor.debitarSaldo(cobranca.getValor());
        cobranca.setStatusCobranca(StatusCobranca.CANCELADA);
        usuarioRepository.save(devedor);
        usuarioRepository.save(credor);
        cobrancaRepository.save(cobranca);
    }

    private void cobrancaPagaComCartao(Cobranca cobranca) {
        if(!clientAutorizador.solicitarAutorizacao().getData().isAuthorized()){
            throw new RegraDeNegocioException("Cancelamento não autorizado");
        }
        cobranca.setStatusCobranca(StatusCobranca.CANCELADA);
        cobrancaRepository.save(cobranca);
        cobranca.getOriginador().debitarSaldo(cobranca.getValor());
        usuarioRepository.save(cobranca.getOriginador());
    }

}
