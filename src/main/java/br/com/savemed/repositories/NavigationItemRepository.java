package br.com.savemed.repositories;

import br.com.savemed.model.NavigationItem; // Importe a entidade refatorada
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NavigationItemRepository extends JpaRepository<NavigationItem, Long> {

    /**
     * Busca os itens de menu de NÍVEL RAIZ (sem pai) que um usuário específico tem permissão para visualizar.
     * A lógica de permissão é:
     * 1. O usuário tem uma permissão direta em PERMISSAO_ADICIONAL_USUARIO.
     * 2. OU um de seus perfis tem uma permissão em PERMISSAO.
     * 3. E o usuário NÃO tem uma restrição explícita em RESTRICAO_ESPECIFICA_USUARIO.
     * @param pageable Informação de paginação.
     * @param userId ID do usuário legado (tipo Integer).
     * @return Uma página de itens de navegação visíveis para o usuário.
     */
    @Query("SELECT DISTINCT n FROM NavigationItem n " +
            "LEFT JOIN PermissaoAdicionalUsuario pau ON pau.idRecurso = n.id AND pau.tipoRecurso = 'NAVIGATION_ITEM' AND pau.usuario.usuario = :userId AND pau.podeVisualizar = true " +
            "LEFT JOIN Permissao p ON p.idRecurso = n.id AND p.tipoRecurso = 'NAVIGATION_ITEM' AND p.podeVisualizar = true " +
            "LEFT JOIN p.perfil.usuarios up ON up.usuario.usuario = :userId " +
            "LEFT JOIN RestricaoEspecificaUsuario rsu ON rsu.idRecurso = n.id AND rsu.tipoRecurso = 'NAVIGATION_ITEM' AND rsu.usuario.usuario = :userId AND rsu.negarVisualizar = true " +
            "WHERE n.parent IS NULL AND n.active = true " +
            "AND (pau.id IS NOT NULL OR up.id IS NOT NULL) AND rsu.id IS NULL " +
            "ORDER BY n.title ASC")
    Page<NavigationItem> findVisibleMenusForUser(Pageable pageable, @Param("userId") Integer userId);

    /**
     * Busca os itens de menu FILHOS de um item pai específico, aplicando a mesma lógica de permissão.
     * @param pageable Informação de paginação.
     * @param parentId ID do item de menu pai.
     * @param userId ID do usuário legado (tipo Integer).
     * @return Uma página de itens de navegação filhos visíveis para o usuário.
     */
    @Query("SELECT DISTINCT n FROM NavigationItem n " +
            "LEFT JOIN PermissaoAdicionalUsuario pau ON pau.idRecurso = n.id AND pau.tipoRecurso = 'NAVIGATION_ITEM' AND pau.usuario.usuario = :userId AND pau.podeVisualizar = true " +
            "LEFT JOIN Permissao p ON p.idRecurso = n.id AND p.tipoRecurso = 'NAVIGATION_ITEM' AND p.podeVisualizar = true " +
            "LEFT JOIN p.perfil.usuarios up ON up.usuario.usuario = :userId " +
            "LEFT JOIN RestricaoEspecificaUsuario rsu ON rsu.idRecurso = n.id AND rsu.tipoRecurso = 'NAVIGATION_ITEM' AND rsu.usuario.usuario = :userId AND rsu.negarVisualizar = true " +
            "WHERE n.parent.id = :parentId AND n.active = true " +
            "AND (pau.id IS NOT NULL OR up.id IS NOT NULL) AND rsu.id IS NULL " +
            "ORDER BY n.title ASC")
    Page<NavigationItem> findVisibleChildrenForUser(Pageable pageable, @Param("parentId") Long parentId, @Param("userId") Integer userId);

    default Optional<NavigationItem> findByIdWithLogging(Long id) {
        System.out.println("Buscando ID: " + id + " - Tipo: " + id.getClass());
        Optional<NavigationItem> result = findById(id);
        System.out.println("Resultado encontrado: " + result.isPresent());
        return result;
    }
}