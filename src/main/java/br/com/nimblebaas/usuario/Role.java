package br.com.nimblebaas.usuario;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public enum RoleValues{
        ADMIN(1L),
        BASIC(2L);

        final long id;

        RoleValues(long roleId) {
            this.id = roleId;
        }

        public long getRoleId() {
            return id;
        }
    }

}
