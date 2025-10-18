package br.com.nimblebaas.cobranca;

import br.com.nimblebaas.cobranca.dto.CriarCobrancaDTO;
import br.com.nimblebaas.usuario.Usuario;
import br.com.nimblebaas.usuario.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CobrancaService {

    private final CobrancaRepository cobrancaRepository;
    private final UsuarioRepository usuarioRepository;

    public CobrancaService(CobrancaRepository cobrancaRepository, UsuarioRepository usuarioRepository) {
        this.cobrancaRepository = cobrancaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Cobranca criarCobranca(CriarCobrancaDTO cobranca){
        Cobranca cobrancaModel = configuraCobranca(new Cobranca(cobranca));
        return cobrancaRepository.save(cobrancaModel);
    }

    private Cobranca configuraCobranca(Cobranca cobranca){
        cobranca.setOriginador(getUsuarioLogado());
        cobranca.setDestinatario(getUsuarioDestinatario(cobranca.getCpfDestinatario()));
        cobranca.setStatusCobranca(StatusCobranca.PENDENTE);
        return cobranca;
    }

    private Usuario getUsuarioLogado(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
    }

    private Usuario getUsuarioDestinatario(String cpf){
        return usuarioRepository.findByCpf(cpf).orElseThrow();
    }

}
