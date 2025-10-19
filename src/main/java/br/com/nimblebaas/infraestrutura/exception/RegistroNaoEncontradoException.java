package br.com.nimblebaas.infraestrutura.exception;

public class RegistroNaoEncontradoException extends RuntimeException {
    public RegistroNaoEncontradoException(String message) {
        super(message);
    }
}
