package br.com.nimblebaas.domain.cobranca.validacao.pagamento.geral;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;

public interface ValidacaoPagamentoCobranca {
    void validar(Cobranca cobranca);
}
