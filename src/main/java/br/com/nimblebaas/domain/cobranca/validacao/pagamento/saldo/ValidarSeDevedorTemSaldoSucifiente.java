package br.com.nimblebaas.domain.cobranca.validacao.pagamento.saldo;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import org.springframework.stereotype.Component;

@Component
public class ValidarSeDevedorTemSaldoSucifiente implements ValidacaoPagamentoCobrancaSaldo {
    @Override
    public void validar(Cobranca cobranca) {
        if(cobranca.getValor().compareTo(cobranca.getDestinatario().getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente");
        }
    }
}
