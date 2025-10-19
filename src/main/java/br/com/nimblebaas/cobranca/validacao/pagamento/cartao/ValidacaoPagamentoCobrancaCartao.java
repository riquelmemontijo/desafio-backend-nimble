package br.com.nimblebaas.cobranca.validacao.pagamento.cartao;

import br.com.nimblebaas.cobranca.dto.CartaoDeCreditoDTO;

public interface ValidacaoPagamentoCobrancaCartao {
    void validar(CartaoDeCreditoDTO cartao);
}
