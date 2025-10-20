package br.com.nimblebaas.domain.cobranca.model.dto;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;

public record CancelamentoCobrancaDTO(Long idCobranca,
                                      StatusCobranca statusCobranca,
                                      String nomeCredor,
                                      String nomeDevedor,
                                      String mensagem) {
    public CancelamentoCobrancaDTO(Cobranca cobranca, String mensagem) {
        this(cobranca.getId(),
                cobranca.getStatusCobranca(),
                cobranca.getOriginador().getNome(),
                cobranca.getDestinatario().getNome(),
                mensagem);
    }
}
