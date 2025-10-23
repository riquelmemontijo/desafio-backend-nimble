package br.com.nimblebaas.domain.cobranca.validacao.cancelamento;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import br.com.nimblebaas.util.usuario.UsuarioUtils;
import org.springframework.stereotype.Component;

@Component
public class ValidaSeOriginadorIgualQuemEstaCancelando implements ValidacaoCancelamentoCobranca{

    private final UsuarioUtils usuarioUtils;

    public ValidaSeOriginadorIgualQuemEstaCancelando(UsuarioUtils usuarioUtils) {
        this.usuarioUtils = usuarioUtils;
    }

    @Override
    public void validar(Cobranca cobranca) {
        var usuarioLogado = usuarioUtils.getUsuarioLogado();
        if(!cobranca.getOriginador().getId().equals(usuarioLogado.getId())) {
            throw new RegraDeNegocioException("Apenas o credor da cobrança pode cancelar a cobrança");
        }
    }
}
