package br.com.nimblebaas.usuario;

import br.com.nimblebaas.usuario.dto.UsuarioCriacaoRequest;
import jakarta.persistence.*;

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
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "usuario_role",
               joinColumns = @JoinColumn(name = "usuario_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

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
}
