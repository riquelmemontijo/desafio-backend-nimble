package br.com.nimblebaas.domain.usuario.controller;

import br.com.nimblebaas.domain.usuario.model.dto.*;
import br.com.nimblebaas.domain.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UsuarioCriacaoResponseDTO> signUp(@RequestBody @Valid UsuarioCriacaoRequestDTO usuarioCriacaoRequestDTO){
        return ResponseEntity.ok(usuarioService.signUp(usuarioCriacaoRequestDTO));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<UsuarioLoginResponseDTO> signIn(@RequestBody UsuarioLoginRequestDTO usuarioLoginRequestDTO) {
        return ResponseEntity.ok(usuarioService.signIn(usuarioLoginRequestDTO));
    }

    @PostMapping("/fazer-deposito")
    public ResponseEntity<DepositoResponseDTO> fazerDeposito(@RequestBody @Valid DepositoRequestDTO deposito){
        return ResponseEntity.ok(usuarioService.fazerDeposito(deposito));
    }

}
