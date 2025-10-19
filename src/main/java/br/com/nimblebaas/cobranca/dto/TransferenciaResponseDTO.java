package br.com.nimblebaas.cobranca.dto;

import br.com.nimblebaas.cobranca.Cobranca;
import br.com.nimblebaas.cobranca.StatusCobranca;
import br.com.nimblebaas.usuario.Usuario;

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
