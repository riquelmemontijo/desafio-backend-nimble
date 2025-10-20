package br.com.nimblebaas.domain.cobranca.service;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.dto.CriarCobrancaRequestDTO;
import br.com.nimblebaas.domain.cobranca.model.dto.CriarCobrancaResponseDTO;
import br.com.nimblebaas.domain.cobranca.repository.CobrancaRepository;
import br.com.nimblebaas.domain.cobranca.validacao.criacao.ValidacaoCriacaoCobranca;
import br.com.nimblebaas.util.usuario.UsuarioUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CriarCobrancaService {

    private final CobrancaRepository cobrancaRepository;
    private final List<ValidacaoCriacaoCobranca> validacaoCriacaoCobranca;
    private final UsuarioUtils usuarioUtils;

    public CriarCobrancaService(CobrancaRepository cobrancaRepository, List<ValidacaoCriacaoCobranca> validacaoCriacaoCobranca, UsuarioUtils usuarioUtils) {
        this.cobrancaRepository = cobrancaRepository;
        this.validacaoCriacaoCobranca = validacaoCriacaoCobranca;
        this.usuarioUtils = usuarioUtils;
    }

    @Transactional
    public CriarCobrancaResponseDTO criarCobranca(CriarCobrancaRequestDTO cobranca){
        Cobranca cobrancaModel = configurarCobrancaParaCadastro(new Cobranca(cobranca));
        validacaoCriacaoCobranca.forEach(validacao -> validacao.validar(cobrancaModel));
        return new CriarCobrancaResponseDTO(cobrancaRepository.save(cobrancaModel));
    }

    private Cobranca configurarCobrancaParaCadastro(Cobranca cobranca){
        cobranca.setOriginador(usuarioUtils.getUsuarioLogado());
        cobranca.setDestinatario(usuarioUtils.buscarUsuarioPorCpf(cobranca.getCpfDestinatario()));
        cobranca.setStatusCobranca(StatusCobranca.PENDENTE);
        return cobranca;
    }

}
