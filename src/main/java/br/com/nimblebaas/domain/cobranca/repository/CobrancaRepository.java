package br.com.nimblebaas.domain.cobranca.repository;

import br.com.nimblebaas.domain.cobranca.model.Cobranca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CobrancaRepository extends JpaRepository<Cobranca, Long>, JpaSpecificationExecutor<Cobranca> {
}
