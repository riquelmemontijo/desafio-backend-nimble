package br.com.nimblebaas.cobranca;

import br.com.nimblebaas.cobranca.dto.CriarCobrancaDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cobrancas")
public class CobrancaController {

    private final CobrancaService cobrancaService;

    public CobrancaController(CobrancaService cobrancaService) {
        this.cobrancaService = cobrancaService;
    }

    @PostMapping("/criar-cobranca")
    public ResponseEntity<Cobranca> criarCobranca(@RequestBody @Valid CriarCobrancaDTO cobranca){
        return ResponseEntity.ok(cobrancaService.criarCobranca(cobranca));
    }

}
