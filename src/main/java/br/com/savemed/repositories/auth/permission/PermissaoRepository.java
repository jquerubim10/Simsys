package br.com.savemed.repositories.auth.permission;

import br.com.savemed.model.auth.permission.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, Long> {
    // JpaRepository<Entidade, TipoDoId>
}