package br.com.nimblebaas.cobranca.dto;

import br.com.nimblebaas.cobranca.Cobranca;
import br.com.nimblebaas.cobranca.StatusCobranca;

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
