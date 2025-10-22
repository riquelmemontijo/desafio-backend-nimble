package br.com.nimblebaas.domain.cobranca.service;

import br.com.nimblebaas.domain.cobranca.model.cancelamentostrategy.SeletorDeEstrategiaDeCancelamento;
import br.com.nimblebaas.domain.cobranca.model.dto.CancelamentoCobrancaDTO;
import br.com.nimblebaas.domain.cobranca.repository.CobrancaRepository;
import br.com.nimblebaas.domain.cobranca.validacao.cancelamento.ValidacaoCancelamentoCobranca;
import br.com.nimblebaas.infraestrutura.exception.RegistroNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CancelarCobrancaService {

    private final CobrancaRepository cobrancaRepository;
    private final SeletorDeEstrategiaDeCancelamento seletorDeEstrategiaDeCancelamento;
    private final List<ValidacaoCancelamentoCobranca> validacoesCancelamentoCobranca;

    public CancelarCobrancaService(CobrancaRepository cobrancaRepository, SeletorDeEstrategiaDeCancelamento seletorDeEstrategiaDeCancelamento, List<ValidacaoCancelamentoCobranca> validacoesCancelamentoCobranca) {
        this.cobrancaRepository = cobrancaRepository;
        this.seletorDeEstrategiaDeCancelamento = seletorDeEstrategiaDeCancelamento;
        this.validacoesCancelamentoCobranca = validacoesCancelamentoCobranca;
    }

    @Transactional
    public CancelamentoCobrancaDTO cancelarCobranca(Long idCobranca){
        var cobranca = cobrancaRepository.findById(idCobranca)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Cobranca não foi localizada no sistema"));
        validacoesCancelamentoCobranca.forEach(validacao -> validacao.validar(cobranca));
        seletorDeEstrategiaDeCancelamento.executarEstrategia(cobranca);

        var mensagem = "Cancelamento realizado com sucesso.";
        return new CancelamentoCobrancaDTO(cobranca, mensagem);
    }

}
