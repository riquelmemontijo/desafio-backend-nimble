package br.com.nimblebaas.domain.cobranca.model.dto;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.usuario.model.dto.UsuarioCobrancaResponseDTO;

import java.math.BigDecimal;

public record CobrancaResponseDTO(Long id,
                                  UsuarioCobrancaResponseDTO originador,
                                  UsuarioCobrancaResponseDTO destinatario,
                                  String descricao,
                                  StatusCobranca status,
                                  BigDecimal valor) {
    public CobrancaResponseDTO(Cobranca cobranca) {
        this(cobranca.getId(),
             new UsuarioCobrancaResponseDTO(cobranca.getOriginador()),
             new UsuarioCobrancaResponseDTO(cobranca.getDestinatario()),
             cobranca.getDescricao(), cobranca.getStatusCobranca(),
             cobranca.getValor());
    }
}
