package br.com.nimblebaas.domain.cobranca.service;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.TipoRelacaoCobranca;
import br.com.nimblebaas.domain.cobranca.model.dto.CobrancaResponseDTO;
import br.com.nimblebaas.domain.cobranca.repository.CobrancaRepository;
import br.com.nimblebaas.domain.cobranca.repository.CobrancaSpecification;
import br.com.nimblebaas.util.usuario.UsuarioUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListarCobrancasService {

    private final UsuarioUtils usuarioUtils;
    private final CobrancaRepository cobrancaRepository;

    public ListarCobrancasService(UsuarioUtils usuarioUtils, CobrancaRepository cobrancaRepository) {
        this.usuarioUtils = usuarioUtils;
        this.cobrancaRepository = cobrancaRepository;
    }

    public Page<CobrancaResponseDTO> listarCobrancas(Pageable pageable, StatusCobranca status, TipoRelacaoCobranca tipoRelacaoCobranca){
        var usuario = usuarioUtils.getUsuarioLogado();
        Page<Cobranca> cobrancas = cobrancaRepository.findAll(CobrancaSpecification.porStatusDaCobranca(status)
                .and(CobrancaSpecification.porRelacaoComACobranca(usuario, tipoRelacaoCobranca)), pageable);
        return cobrancas.map(CobrancaResponseDTO::new);
    }

}
