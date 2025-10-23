package br.com.nimblebaas.domain.cobranca.repository;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.TipoRelacaoCobranca;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import org.springframework.data.jpa.domain.Specification;

public class CobrancaSpecification {

    public static Specification<Cobranca> porRelacaoComACobranca(Usuario usuario, TipoRelacaoCobranca tipoRelacaoCobranca) {
        return (root, query, cb) -> {
            if(tipoRelacaoCobranca == TipoRelacaoCobranca.ORIGINADOR) {
                return cb.equal(root.get("originador"), usuario);
            } else if (tipoRelacaoCobranca == TipoRelacaoCobranca.DESTINATARIO) {
                return cb.equal(root.get("destinatario"), usuario);
            } else {
                return cb.or(cb.equal(root.get("originador"), usuario), cb.equal(root.get("destinatario"), usuario));
            }
        };
    }


    public static Specification<Cobranca> porStatusDaCobranca(StatusCobranca statusCobranca) {
        return (root, query, cb) ->
                statusCobranca == null ? null : cb.equal(root.get("statusCobranca"), statusCobranca);
    }

}
