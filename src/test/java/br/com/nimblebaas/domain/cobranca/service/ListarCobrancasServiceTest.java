package br.com.nimblebaas.domain.cobranca.service;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import br.com.nimblebaas.domain.cobranca.model.StatusCobranca;
import br.com.nimblebaas.domain.cobranca.model.TipoRelacaoCobranca;
import br.com.nimblebaas.domain.cobranca.model.dto.CobrancaResponseDTO;
import br.com.nimblebaas.domain.cobranca.repository.CobrancaRepository;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.util.usuario.UsuarioUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarCobrancasServiceTest {

    @Mock
    private UsuarioUtils usuarioUtils;

    @Mock
    private CobrancaRepository cobrancaRepository;

    @InjectMocks
    private ListarCobrancasService listarCobrancasService;

    private Usuario usuarioOriginador;
    private Usuario usuarioDestinatario;
    private Pageable pageable;
    private Cobranca cobranca;

    @BeforeEach
    void setUp() {
        usuarioOriginador = new Usuario();
        usuarioOriginador.setId(1L);
        usuarioOriginador.setNome("Usuário Originador");

        usuarioDestinatario = new Usuario();
        usuarioDestinatario.setId(2L);
        usuarioDestinatario.setNome("Usuário Destinatário");

        pageable = PageRequest.of(0, 10);

        cobranca = new Cobranca();
        cobranca.setId(1L);
        cobranca.setValor(new BigDecimal("100.00"));
        cobranca.setStatusCobranca(StatusCobranca.PENDENTE);
        cobranca.setOriginador(usuarioOriginador);
        cobranca.setDestinatario(usuarioDestinatario);

    }

    @Test
    @DisplayName("Deve listar cobranças e retornar DTOs com sucesso")
    void deveListarCobrancasComSucesso() {
        // arrange
        List<Cobranca> cobrancas = Collections.singletonList(cobranca);
        Page<Cobranca> cobrancaPage = new PageImpl<>(cobrancas, pageable, 1);

        when(usuarioUtils.getUsuarioLogado()).thenReturn(usuarioOriginador);
        when(cobrancaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(cobrancaPage);

        // act
        Page<CobrancaResponseDTO> result = listarCobrancasService.listarCobrancas(pageable, StatusCobranca.PENDENTE, TipoRelacaoCobranca.DESTINATARIO);

        // assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        CobrancaResponseDTO dto = result.getContent().getFirst();
        assertEquals(cobranca.getId(), dto.id());
        assertEquals(cobranca.getValor(), dto.valor());
        assertEquals(cobranca.getStatusCobranca(), dto.status());

        verify(usuarioUtils).getUsuarioLogado();
        verify(cobrancaRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar uma página vazia quando não houver cobranças")
    void deveRetornarPaginaVazia() {
        when(usuarioUtils.getUsuarioLogado()).thenReturn(usuarioOriginador);
        when(cobrancaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty(pageable));

        Page<CobrancaResponseDTO> result = listarCobrancasService.listarCobrancas(pageable, StatusCobranca.PAGA, TipoRelacaoCobranca.ORIGINADOR);

        assertTrue(result.isEmpty());
    }
}