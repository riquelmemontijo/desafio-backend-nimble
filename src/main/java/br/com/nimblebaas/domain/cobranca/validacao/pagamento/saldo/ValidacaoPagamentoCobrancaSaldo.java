package br.com.nimblebaas.domain.cobranca.validacao.pagamento.saldo;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;

public interface ValidacaoPagamentoCobrancaSaldo {
    void validar(Cobranca cobranca);
}
