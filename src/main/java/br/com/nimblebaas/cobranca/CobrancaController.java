package br.com.nimblebaas.cobranca;

import br.com.nimblebaas.cobranca.dto.CriarCobrancaRequestDTO;
import br.com.nimblebaas.cobranca.dto.CriarCobrancaResponseDTO;
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
    public ResponseEntity<CriarCobrancaResponseDTO> criarCobranca(@RequestBody @Valid CriarCobrancaRequestDTO cobranca){
        return ResponseEntity.ok(cobrancaService.criarCobranca(cobranca));
    }

}
