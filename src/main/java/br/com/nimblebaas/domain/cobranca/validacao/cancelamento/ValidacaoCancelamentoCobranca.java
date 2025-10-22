package br.com.nimblebaas.domain.cobranca.validacao.cancelamento;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;

public interface ValidacaoCancelamentoCobranca {
    void validar(Cobranca cobranca);
}
