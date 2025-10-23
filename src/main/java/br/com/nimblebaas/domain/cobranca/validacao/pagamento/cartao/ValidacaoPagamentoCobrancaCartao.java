package br.com.nimblebaas.domain.cobranca.validacao.pagamento.cartao;

import br.com.nimblebaas.domain.cobranca.model.dto.CartaoDeCreditoRequestDTO;

public interface ValidacaoPagamentoCobrancaCartao {
    void validar(CartaoDeCreditoRequestDTO cartao);
}
