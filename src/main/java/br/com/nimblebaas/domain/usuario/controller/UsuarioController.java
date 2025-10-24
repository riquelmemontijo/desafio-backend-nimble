package br.com.nimblebaas.domain.usuario.controller;

import br.com.nimblebaas.domain.usuario.model.dto.*;
import br.com.nimblebaas.domain.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Cadastrar usuario", description = "Cadastra um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "A requisição não pode ser processada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "422", description = "Dados informados erroneamente no body ou no recurso da URL"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<UsuarioCriacaoResponseDTO> signUp(@RequestBody @Valid UsuarioCriacaoRequestDTO usuarioCriacaoRequestDTO){
        var usuario = usuarioService.signUp(usuarioCriacaoRequestDTO);
        URI location = URI.create("/usuarios/" + usuario.id());
        return ResponseEntity.created(location).body(usuario);
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Login de usuario", description = "Faz login do usuario na api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "A requisição não pode ser processada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "422", description = "Dados informados erroneamente no body ou no recurso da URL"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<UsuarioLoginResponseDTO> signIn(@RequestBody UsuarioLoginRequestDTO usuarioLoginRequestDTO) {
        return ResponseEntity.ok(usuarioService.signIn(usuarioLoginRequestDTO));
    }

    @PostMapping("/fazer-deposito")
    @Operation(summary = "Depósito monetário", description = "Faz depósito no saldo do usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "A requisição não pode ser processada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "422", description = "Dados informados erroneamente no body ou no recurso da URL"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<DepositoResponseDTO> fazerDeposito(@RequestBody @Valid DepositoRequestDTO deposito){
        return ResponseEntity.ok(usuarioService.fazerDeposito(deposito));
    }

}
