package br.com.nimblebaas.cobranca;

import br.com.nimblebaas.cobranca.dto.*;
import br.com.nimblebaas.infraestrutura.exception.RegraDeNegocioException;
import br.com.nimblebaas.servicos.ClientAutorizador;
import br.com.nimblebaas.usuario.Usuario;
import br.com.nimblebaas.usuario.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CobrancaService {

    private final CobrancaRepository cobrancaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClientAutorizador clientAutorizador;

    public CobrancaService(CobrancaRepository cobrancaRepository, UsuarioRepository usuarioRepository, ClientAutorizador clientAutorizador) {
        this.cobrancaRepository = cobrancaRepository;
        this.usuarioRepository = usuarioRepository;
        this.clientAutorizador = clientAutorizador;
    }

    @Transactional
    public CriarCobrancaResponseDTO criarCobranca(CriarCobrancaRequestDTO cobranca){
        Cobranca cobrancaModel = configuraCobranca(new Cobranca(cobranca));
        return new CriarCobrancaResponseDTO(cobrancaRepository.save(cobrancaModel));
    }

    public Page<CobrancaResponseDTO> obterCobrancas(Pageable pageable, StatusCobranca status, TipoRelacaoCobranca tipoRelacaoCobranca){
        var usuario = getUsuarioLogado();
        Page<Cobranca> cobrancas = cobrancaRepository.findAll(CobrancaSpecification.porStatusDaCobranca(status)
                .and(CobrancaSpecification.porRelacaoComACobranca(usuario, tipoRelacaoCobranca)), pageable);
        return cobrancas.map(CobrancaResponseDTO::new);
    }

    @Transactional
    public TransferenciaResponseDTO pagarCobrancaComSaldo(Long idCobranca){
        var cobranca = cobrancaRepository.findById(idCobranca).orElseThrow();
        var devedor = getUsuarioLogado();
        validarSaldoSuficiente(devedor, cobranca);
        var credor = buscarUsuarioPorCpf(cobranca.getOriginador().getCpf());
        realizarTransferencia(devedor, credor, cobranca.getValor());
        cobranca.setStatusCobranca(StatusCobranca.PAGA);
        cobrancaRepository.save(cobranca);
        var mensagem = "Transação realizada com sucesso. Cobranda paga!";
        return new TransferenciaResponseDTO(cobranca, credor, devedor, mensagem);
    }

    public String pagarCobrancaComCartaoDeCredito(Long idCobranca, CartaoDeCreditoDTO cartao){
        var response = clientAutorizador.solicitarAutorizacao();
        if (!response.getData().isAuthorized()) {
            throw new RegraDeNegocioException("Transação não autorizada");
        }
        var cobranca = cobrancaRepository.findById(idCobranca).orElseThrow();
        cobranca.setStatusCobranca(StatusCobranca.PAGA);
        cobrancaRepository.save(cobranca);
        return "Pagamento realizado com sucesso.";
    }

    private Cobranca configuraCobranca(Cobranca cobranca){
        cobranca.setOriginador(getUsuarioLogado());
        cobranca.setDestinatario(buscarUsuarioPorCpf(cobranca.getCpfDestinatario()));
        cobranca.setStatusCobranca(StatusCobranca.PENDENTE);
        return cobranca;
    }

    private Usuario getUsuarioLogado(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
    }

    private Usuario buscarUsuarioPorCpf(String cpf){
        return usuarioRepository.findByCpf(cpf).orElseThrow();
    }

    private void validarSaldoSuficiente(Usuario usuario, Cobranca cobranca){
        if(usuario.getSaldo().compareTo(cobranca.getValor()) < 0){
            throw new RegraDeNegocioException("Saldo insuficiente");
        }
    }

    private void realizarTransferencia(Usuario devedor, Usuario credor, BigDecimal valor) {
        devedor.debitarSaldo(valor);
        credor.creditarSaldo(valor);
        usuarioRepository.save(devedor);
        usuarioRepository.save(credor);
    }

}
