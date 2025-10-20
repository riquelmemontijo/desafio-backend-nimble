package br.com.nimblebaas.domain.usuario.model;

import br.com.nimblebaas.domain.role.role.Role;
import br.com.nimblebaas.domain.usuario.model.dto.UsuarioCriacaoRequest;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity(name = "Usuario")
@Table(name = "usuario")
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false, length = 11)
    private String cpf;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, length = 80)
    private String senha;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_role",
               joinColumns = @JoinColumn(name = "usuario_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
    @Column(nullable = false, precision = 20, scale = 2)
    @Access(AccessType.FIELD)
    private BigDecimal saldo;

    @PrePersist
    private void inicializarValoresPadroes() {
        if (this.saldo == null) {
            this.saldo = BigDecimal.ZERO;
        }
    }

    public Usuario() {
    }

    public Usuario(UsuarioCriacaoRequest usuario) {
        this.email = usuario.email();
        this.cpf = usuario.cpf();
        this.nome = usuario.nome();
        this.senha = usuario.senha();
    }

    public Usuario(Long id, String email, String cpf, String nome, String senha, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.cpf = cpf;
        this.nome = nome;
        this.senha = senha;
        this.roles = roles;
        this.saldo = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void creditarSaldo(BigDecimal valor) {
        this.saldo = this.saldo.add(valor);
    }

    public void debitarSaldo(BigDecimal valor) {
        if(this.saldo.compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }
        this.saldo = this.saldo.subtract(valor);
    }
}
