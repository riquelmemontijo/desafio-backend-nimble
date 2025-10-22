package br.com.nimblebaas.domain.cobranca.validacao.cancelamento;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import org.springframework.stereotype.Component;

@Component
public class ValidaSeCobrancaEstaCancelada implements ValidacaoCancelamentoCobranca{
    @Override
    public void validar(Cobranca cobranca) {
        if(cobranca.getStatusCobranca() == StatusCobranca.CANCELADA){
            throw new RuntimeException("Cobrança já está cancelada");
        }
    }
}
