package br.com.nimblebaas.domain.cobranca.model.dto;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.usuario.model.Usuario;

import java.math.BigDecimal;

public record TransferenciaResponseDTO(Long idCobranca,
                                       BigDecimal valorPago,
                                       StatusCobranca statusCobranca,
                                       String credor,
                                       String devedor,
                                       String mensagem){
    public TransferenciaResponseDTO(Cobranca cobranca, Usuario credor, Usuario devedor, String mensagem) {
        this(cobranca.getId(), cobranca.getValor(), cobranca.getStatusCobranca(), credor.getNome(), devedor.getNome(), mensagem);
    }
}
