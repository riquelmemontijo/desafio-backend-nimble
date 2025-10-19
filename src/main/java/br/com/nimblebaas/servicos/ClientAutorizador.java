package br.com.nimblebaas.servicos;

import br.com.nimblebaas.infraestrutura.exception.ClientAutorizadorException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Service
public class ClientAutorizador {

    private final RestClient restClient;

    public ClientAutorizador() {
        URI uri = URI.create("https://zsy6tx7aql.execute-api.sa-east-1.amazonaws.com/authorizer");
        this.restClient = RestClient.builder()
                .baseUrl(uri)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) ->{
                    throw new ClientAutorizadorException("Erro ao solicitar autorização");
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) ->{
                    throw new ClientAutorizadorException("Serviço de autorização está indisponível. Tente mais tarde");
                })
                .build();
    }

    public AutorizadorResponse solicitarAutorizacao(){
        return this.restClient.get().retrieve().body(AutorizadorResponse.class);
    }

}
