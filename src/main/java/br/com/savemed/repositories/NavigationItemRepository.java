package br.com.savemed.repositories;

import br.com.savemed.model.NavigationItem; // Importe a entidade refatorada
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    @Query(value = "SELECT n FROM NavigationItem n WHERE n.parent IS NULL AND n.active = true AND " +
            "(" +
            "   EXISTS (SELECT 1 FROM PermissaoAdicionalUsuario pau WHERE pau.usuario.usuario = :userId AND pau.idRecurso = n.id AND pau.tipoRecurso = 'NAVIGATION_ITEM' AND pau.podeVisualizar = true)" +
            "   OR " +
            "   EXISTS (SELECT 1 FROM Permissao p JOIN p.perfil.usuarios up WHERE up.usuario.usuario = :userId AND p.idRecurso = n.id AND p.tipoRecurso = 'NAVIGATION_ITEM' AND p.podeVisualizar = true)" +
            ") AND NOT EXISTS (SELECT 1 FROM RestricaoEspecificaUsuario rsu WHERE rsu.usuario.usuario = :userId AND rsu.idRecurso = n.id AND rsu.tipoRecurso = 'NAVIGATION_ITEM' AND rsu.negarVisualizar = true)" +
            "ORDER BY n.title ASC")
    Page<NavigationItem> findVisibleMenusForUser(Pageable pageable, @Param("userId") Integer userId);


    /**
     * Busca os itens de menu FILHOS de um item pai específico, aplicando a mesma lógica de permissão.
     * @param pageable Informação de paginação.
     * @param parentId ID do item de menu pai.
     * @param userId ID do usuário legado (tipo Integer).
     * @return Uma página de itens de navegação filhos visíveis para o usuário.
     */
    @Query(value = "SELECT n FROM NavigationItem n WHERE n.parent.id = :parentId AND n.active = true AND " +
            "(" +
            "   EXISTS (SELECT 1 FROM PermissaoAdicionalUsuario pau WHERE pau.usuario.usuario = :userId AND pau.idRecurso = n.id AND pau.tipoRecurso = 'NAVIGATION_ITEM' AND pau.podeVisualizar = true)" +
            "   OR " +
            "   EXISTS (SELECT 1 FROM Permissao p JOIN p.perfil.usuarios up WHERE up.usuario.usuario = :userId AND p.idRecurso = n.id AND p.tipoRecurso = 'NAVIGATION_ITEM' AND p.podeVisualizar = true)" +
            ") AND NOT EXISTS (SELECT 1 FROM RestricaoEspecificaUsuario rsu WHERE rsu.usuario.usuario = :userId AND rsu.idRecurso = n.id AND rsu.tipoRecurso = 'NAVIGATION_ITEM' AND rsu.negarVisualizar = true)" +
            "ORDER BY n.title ASC")
    Page<NavigationItem> findVisibleChildrenForUser(Pageable pageable, @Param("parentId") Long parentId, @Param("userId") Integer userId);

    // O método disableMenu foi removido daqui. É uma prática melhor que essa lógica
    // de negócio (desabilitar um menu) fique na camada de Serviço, que pode então
    // chamar o repository.save(entity) para persistir a alteração.
}