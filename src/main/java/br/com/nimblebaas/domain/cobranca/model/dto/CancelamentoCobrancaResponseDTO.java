package br.com.nimblebaas.domain.cobranca.model.dto;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;

public record CancelamentoCobrancaResponseDTO(Long idCobranca,
                                              StatusCobranca statusCobranca,
                                              String nomeCredor,
                                              String nomeDevedor,
                                              String mensagem) {
    public CancelamentoCobrancaResponseDTO(Cobranca cobranca, String mensagem) {
        this(cobranca.getId(),
                cobranca.getStatusCobranca(),
                cobranca.getOriginador().getNome(),
                cobranca.getDestinatario().getNome(),
                mensagem);
    }
}
