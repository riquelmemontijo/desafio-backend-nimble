package br.com.nimblebaas.domain.cobranca.validacao.pagamento.geral;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import org.springframework.stereotype.Component;

@Component
public class ValidarSeCobrancaEstaPendente implements ValidacaoPagamentoCobranca{
    @Override
    public void validar(Cobranca cobranca) {
        if(!cobranca.getStatusCobranca().equals(StatusCobranca.PENDENTE)){
            throw new RegraDeNegocioException("Somente cobrancas pendentes podem ser pagas");
        }
    }
}
