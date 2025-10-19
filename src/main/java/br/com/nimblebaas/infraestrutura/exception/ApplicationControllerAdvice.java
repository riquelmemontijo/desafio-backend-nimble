package br.com.nimblebaas.infraestrutura.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        var fieldErrors = e.getFieldErrors()
                .stream()
                .map(f -> new ParametroInvalido(f.getField(), f.getDefaultMessage()))
                .toList();

        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Os parâmetros da requisição são inválidos");
        problemDetail.setProperty("Parâmetros inválidos", fieldErrors);

        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleNotFound(BadCredentialsException ex) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Unauthorized");
        problemDetail.setDetail("Credenciais inválidas.");
        return problemDetail;
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleNotFound(RegraDeNegocioException ex) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problemDetail.setTitle("A requisição não pode ser processada");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    private record ParametroInvalido(String nome, String motivo){}

}
