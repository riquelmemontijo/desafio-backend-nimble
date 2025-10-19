package br.com.nimblebaas.cobranca.validacao.criacao;

import br.com.nimblebaas.cobranca.Cobranca;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import org.springframework.stereotype.Component;

@Component
public class ValidarSeCredorDiferenteDoDevedor implements ValidacaoCriacaoCobranca {
    @Override
    public void validar(Cobranca cobranca) {
        if(cobranca.getOriginador().equals(cobranca.getDestinatario())){
            throw new RegraDeNegocioException("Credor deve ser diferente do devedor");
        }
    }
}
