package br.com.nimblebaas.domain.cobranca.validacao.pagamento.cartao;

import br.com.nimblebaas.domain.cobranca.model.dto.CartaoDeCreditoDTO;

public interface ValidacaoPagamentoCobrancaCartao {
    void validar(CartaoDeCreditoDTO cartao);
}
