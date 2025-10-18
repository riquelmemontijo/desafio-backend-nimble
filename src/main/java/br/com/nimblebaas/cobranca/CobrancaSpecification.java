package br.com.nimblebaas.cobranca;

import br.com.nimblebaas.usuario.Usuario;
import org.springframework.data.jpa.domain.Specification;

public class CobrancaSpecification {

    public static Specification<Cobranca> porRelacaoComACobranca(Usuario usuario, TipoRelacaoCobranca tipoRelacaoCobranca) {
        return (root, query, cb) -> {
            if(tipoRelacaoCobranca == TipoRelacaoCobranca.ORIGINADOR) {
                return cb.equal(root.get("originador"), usuario);
            } else {
                return cb.equal(root.get("originador"), usuario);
            }
        };
    }

    public static Specification<Cobranca> porStatusDaCobranca(StatusCobranca statusCobranca) {
        return (root, query, cb) ->
                statusCobranca == null ? null : cb.equal(root.get("statusCobranca"), statusCobranca);
    }

}
