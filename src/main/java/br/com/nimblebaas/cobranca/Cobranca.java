package br.com.nimblebaas.cobranca;

import br.com.nimblebaas.cobranca.dto.CriarCobrancaRequestDTO;
import br.com.nimblebaas.usuario.Usuario;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity(name = "Cobranca")
@Table(name = "cobranca")
public class Cobranca {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Usuario destinatario;
    @Column(nullable = false, length = 11)
    private String cpfDestinatario;
    @ManyToOne
    private Usuario originador;
    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatusCobranca statusCobranca;
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private FormaDePagamento formaDePagamento;

    public Cobranca() {
    }

    public Cobranca(CriarCobrancaRequestDTO cobranca) {
        this.cpfDestinatario = cobranca.cpfDestinatario();
        this.valor = cobranca.valor();
        this.descricao = cobranca.descricao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Usuario destinatario) {
        this.destinatario = destinatario;
    }

    public String getCpfDestinatario() {
        return cpfDestinatario;
    }

    public void setCpfDestinatario(String cpfDestinatario) {
        this.cpfDestinatario = cpfDestinatario;
    }

    public Usuario getOriginador() {
        return originador;
    }

    public void setOriginador(Usuario originador) {
        this.originador = originador;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public StatusCobranca getStatusCobranca() {
        return statusCobranca;
    }

    public void setStatusCobranca(StatusCobranca statusCobranca) {
        this.statusCobranca = statusCobranca;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public FormaDePagamento getFormaDePagamento() {
        return formaDePagamento;
    }

    public void setFormaDePagamento(FormaDePagamento formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }
}
