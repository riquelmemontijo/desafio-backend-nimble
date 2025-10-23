package br.com.nimblebaas.domain.cobranca.controller;

import br.com.nimblebaas.domain.cobranca.model.dto.*;
import br.com.nimblebaas.domain.cobranca.service.*;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.TipoRelacaoCobranca;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Criar uma nova cobrança", description = "Cria uma nova cobrança para um destinatário a partir do CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cobrança criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "A requisição não pode ser processada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "422", description = "Dados informados erroneamente no body ou no recurso da URL"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CriarCobrancaResponseDTO> criarCobranca(@RequestBody @Valid CriarCobrancaRequestDTO cobranca){
        return ResponseEntity.ok(criarCobrancaService.criarCobranca(cobranca));
    }


    @GetMapping("/consultar-cobrancas")
    @Operation(summary = "Consultar cobranças", description = "Lista as cobranças com filtro de status e relação do usuario com a cobranca (ORIGINADOR, DESTINATARIO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem paginada das cobranças retornadas"),
            @ApiResponse(responseCode = "400", description = "A requisição não pode ser processada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "422", description = "Dados informados erroneamente no body ou no recurso da URL"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<CobrancaResponseDTO>> obterCobrancas(@PageableDefault Pageable pageable,
                                                                    @RequestParam(required = false) StatusCobranca status,
                                                                    @RequestParam(required = false) TipoRelacaoCobranca tipoRelacaoCobranca){
        return ResponseEntity.ok(listarCobrancasService.listarCobrancas(pageable, status, tipoRelacaoCobranca));
    }

    @PostMapping("/pagar-com-saldo")
    @Operation(summary = "Pagar cobrancça com saldo", description = "Realiza o pagamento de uma cobrança com saldo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cobrança paga com sucesso"),
            @ApiResponse(responseCode = "400", description = "A requisição não pode ser processada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "422", description = "Dados informados erroneamente no body ou no recurso da URL"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<TransferenciaResponseDTO> pagarCobrancaComSaldo(@RequestParam Long idCobranca){
        return ResponseEntity.ok(pagarCobrancaService.pagarCobrancaComSaldo(idCobranca));
    }

    @PostMapping("/pagar-com-cartao")
    @Operation(summary = "Pagar cobrança com cartão de crédito", description = "Realiza o pagamento de uma cobrança com cartão de crédito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cobrança paga com sucesso"),
            @ApiResponse(responseCode = "400", description = "A requisição não pode ser processada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "422", description = "Dados informados erroneamente no body ou no recurso da URL"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<TransferenciaResponseDTO> pagarCobrancaComCartaoDeCredito(@RequestParam Long idCobranca, @RequestBody @Valid CartaoDeCreditoRequestDTO cartao){
        return ResponseEntity.ok(pagarCobrancaService.pagarCobrancaComCartaoDeCredito(idCobranca, cartao));
    }

    @PutMapping("/cancelar")
    @Operation(summary = "Cancelar cobrança", description = "Cancela uma cobrança")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cobrança cancelada com sucesso"),
            @ApiResponse(responseCode = "400", description = "A requisição não pode ser processada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "422", description = "Dados informados erroneamente no body ou no recurso da URL"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CancelamentoCobrancaResponseDTO> cancelarCobranca(@RequestParam Long idCobranca){
        return ResponseEntity.ok(cancelarCobrancaService.cancelarCobranca(idCobranca));
    }

}
