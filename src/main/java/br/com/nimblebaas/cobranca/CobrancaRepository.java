package br.com.nimblebaas.cobranca;

import br.com.nimblebaas.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CobrancaRepository extends JpaRepository<Cobranca, Long>, JpaSpecificationExecutor<Cobranca> {
    @Query("SELECT c FROM Cobranca c WHERE c.originador = :usuario")
    Page<Cobranca> obterCobrancasAReceber(Usuario usuario, Pageable pageable);
    @Query("SELECT c FROM Cobranca c WHERE c.destinatario = :usuario")
    Page<Cobranca> obterCobrancasAPagar(Usuario usuario, Pageable pageable);
}
