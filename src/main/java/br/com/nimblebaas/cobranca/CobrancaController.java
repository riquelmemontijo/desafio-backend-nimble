package br.com.nimblebaas.cobranca;

import br.com.nimblebaas.cobranca.dto.CriarCobrancaRequestDTO;
import br.com.nimblebaas.cobranca.dto.CriarCobrancaResponseDTO;
import br.com.nimblebaas.cobranca.dto.CobrancaResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/consultar-cobrancas")
    public ResponseEntity<Page<CobrancaResponseDTO>> obterCobrancas(@PageableDefault Pageable pageable,
                                                                    @RequestParam(required = false) StatusCobranca status,
                                                                    @RequestParam(required = false) TipoRelacaoCobranca tipoRelacaoCobranca){
        return ResponseEntity.ok(cobrancaService.obterCobrancas(pageable, status, tipoRelacaoCobranca));
    }

}
