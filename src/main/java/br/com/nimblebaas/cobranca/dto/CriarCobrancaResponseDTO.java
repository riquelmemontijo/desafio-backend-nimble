package br.com.nimblebaas.cobranca.dto;

import br.com.nimblebaas.cobranca.Cobranca;
import br.com.nimblebaas.usuario.dto.UsuarioCriarCobrancaResponseDTO;

public record CriarCobrancaResponseDTO(Long id,
                                       UsuarioCriarCobrancaResponseDTO originador,
                                       UsuarioCriarCobrancaResponseDTO destinatario,
                                       String descricao,
                                       String status,
                                       String valor) {
    public CriarCobrancaResponseDTO(Cobranca cobranca){
        this(cobranca.getId(),
             new UsuarioCriarCobrancaResponseDTO(cobranca.getOriginador()),
             new UsuarioCriarCobrancaResponseDTO(cobranca.getDestinatario()),
             cobranca.getDescricao(), cobranca.getStatusCobranca().name(),
             cobranca.getValor().toString());
    }
}
