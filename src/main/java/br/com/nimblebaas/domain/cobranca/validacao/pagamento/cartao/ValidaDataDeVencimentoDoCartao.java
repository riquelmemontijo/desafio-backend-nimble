package br.com.nimblebaas.domain.cobranca.validacao.pagamento.cartao;

import br.com.nimblebaas.domain.cobranca.model.dto.CartaoDeCreditoRequestDTO;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
public class ValidaDataDeVencimentoDoCartao implements ValidacaoPagamentoCobrancaCartao{
    @Override
    public void validar(CartaoDeCreditoRequestDTO cartaoDeCredito) {
        YearMonth yearMonth = YearMonth.parse(cartaoDeCredito.dataValidade(), DateTimeFormatter.ofPattern("MM/yy"));
        if(YearMonth.now().isAfter(yearMonth)){
            throw new RegraDeNegocioException("Cartão com data de validade expirada");
        }
    }
}
