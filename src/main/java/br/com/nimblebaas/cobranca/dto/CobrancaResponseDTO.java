package br.com.nimblebaas.cobranca.dto;

import br.com.nimblebaas.cobranca.Cobranca;
import br.com.nimblebaas.usuario.dto.UsuarioCobrancaResponseDTO;

public record CobrancaResponseDTO(Long id,
                                  UsuarioCobrancaResponseDTO originador,
                                  UsuarioCobrancaResponseDTO destinatario,
                                  String descricao,
                                  String status,
                                  String valor) {
    public CobrancaResponseDTO(Cobranca cobranca) {
        this(cobranca.getId(),
             new UsuarioCobrancaResponseDTO(cobranca.getOriginador()),
             new UsuarioCobrancaResponseDTO(cobranca.getDestinatario()),
             cobranca.getDescricao(), cobranca.getStatusCobranca().name(),
             cobranca.getValor().toString());
    }
}
