package br.com.nimblebaas.domain.role.repository;

import br.com.nimblebaas.domain.role.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByDescricao(String descricao);
}
