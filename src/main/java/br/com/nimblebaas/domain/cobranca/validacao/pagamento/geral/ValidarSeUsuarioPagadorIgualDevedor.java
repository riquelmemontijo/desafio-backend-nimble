package br.com.nimblebaas.domain.cobranca.validacao.pagamento.geral;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import br.com.nimblebaas.util.usuario.UsuarioUtils;
import org.springframework.stereotype.Component;

@Component
public class ValidarSeUsuarioPagadorIgualDevedor implements ValidacaoPagamentoCobranca{

    private final UsuarioUtils usuarioUtils;

    public ValidarSeUsuarioPagadorIgualDevedor(UsuarioUtils usuarioUtils) {
        this.usuarioUtils = usuarioUtils;
    }

    @Override
    public void validar(Cobranca cobranca) {
        if(!cobranca.getDestinatario().equals(usuarioUtils.getUsuarioLogado())){
            throw new RegraDeNegocioException("O solicitande não é o devedor desta cobrança.");
        }
    }
}
