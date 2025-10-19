package br.com.nimblebaas.cobranca.validacao.pagamento.geral;

import br.com.nimblebaas.cobranca.Cobranca;

public interface ValidacaoPagamentoCobranca {
    void validar(Cobranca cobranca);
}
