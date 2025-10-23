package br.com.nimblebaas.domain.cobranca.controller;

import br.com.nimblebaas.domain.cobranca.model.dto.*;
import br.com.nimblebaas.domain.cobranca.service.*;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.TipoRelacaoCobranca;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cobrancas")
public class CobrancaController {

    private final CriarCobrancaService criarCobrancaService;
    private final ListarCobrancasService listarCobrancasService;
    private final PagarCobrancaService pagarCobrancaService;
    private final CancelarCobrancaService cancelarCobrancaService;

    public CobrancaController(CriarCobrancaService criarCobrancaService, ListarCobrancasService listarCobrancasService, PagarCobrancaService pagarCobrancaService, CancelarCobrancaService cancelarCobrancaService) {
        this.criarCobrancaService = criarCobrancaService;
        this.listarCobrancasService = listarCobrancasService;
        this.pagarCobrancaService = pagarCobrancaService;
        this.cancelarCobrancaService = cancelarCobrancaService;
    }

    @PostMapping("/criar-cobranca")
    public ResponseEntity<CriarCobrancaResponseDTO> criarCobranca(@RequestBody @Valid CriarCobrancaRequestDTO cobranca){
        return ResponseEntity.ok(criarCobrancaService.criarCobranca(cobranca));
    }

    @GetMapping("/consultar-cobrancas")
    public ResponseEntity<Page<CobrancaResponseDTO>> obterCobrancas(@PageableDefault Pageable pageable,
                                                                    @RequestParam(required = false) StatusCobranca status,
                                                                    @RequestParam(required = false) TipoRelacaoCobranca tipoRelacaoCobranca){
        return ResponseEntity.ok(listarCobrancasService.listarCobrancas(pageable, status, tipoRelacaoCobranca));
    }

    @PostMapping("/pagar-com-saldo")
    public ResponseEntity<TransferenciaResponseDTO> pagarCobrancaComSaldo(@RequestParam Long idCobranca){
        return ResponseEntity.ok(pagarCobrancaService.pagarCobrancaComSaldo(idCobranca));
    }

    @PostMapping("/pagar-com-cartao")
    public ResponseEntity<TransferenciaResponseDTO> pagarCobrancaComCartaoDeCredito(@RequestParam Long idCobranca, @RequestBody @Valid CartaoDeCreditoRequestDTO cartao){
        return ResponseEntity.ok(pagarCobrancaService.pagarCobrancaComCartaoDeCredito(idCobranca, cartao));
    }

    @PutMapping("/cancelar")
    public ResponseEntity<CancelamentoCobrancaResponseDTO> cancelarCobranca(@RequestParam Long idCobranca){
        return ResponseEntity.ok(cancelarCobrancaService.cancelarCobranca(idCobranca));
    }

}
