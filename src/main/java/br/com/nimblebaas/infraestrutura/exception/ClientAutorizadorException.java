package br.com.nimblebaas.infraestrutura.exception;

public class ClientAutorizadorException extends RuntimeException {
    public ClientAutorizadorException(String message) {
        super(message);
    }
}
