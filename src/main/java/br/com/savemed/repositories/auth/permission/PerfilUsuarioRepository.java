package br.com.savemed.repositories.auth.permission;

import br.com.savemed.model.auth.permission.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, Integer> {
    // JpaRepository<Entidade, TipoDoId>
    // O Spring Data já fornecerá métodos como save(), findAll(), count(), findById(), etc.
}