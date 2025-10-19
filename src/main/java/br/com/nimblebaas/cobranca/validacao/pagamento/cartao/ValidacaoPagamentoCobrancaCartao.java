package br.com.nimblebaas.cobranca.validacao.pagamento.cartao;

import br.com.nimblebaas.cobranca.Cobranca;

public interface ValidacaoPagamentoCobrancaCartao {
    void validar(Cobranca cobranca);
}
