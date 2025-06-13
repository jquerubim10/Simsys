package br.com.savemed.repositories;

import br.com.savemed.model.builder.BuilderScreen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuilderScreenRepository extends JpaRepository<BuilderScreen, Long> {

    /**
     * MÉTODO SEGURO: Busca todas as telas ativas que um usuário pode visualizar.
     * Substitui o antigo e inseguro findAllFilter.
     */
    @Query(value = "SELECT * FROM builder_screen bs WHERE bs.active = true AND (" +
            "EXISTS (SELECT 1 FROM permissao_adicional_usuario pau WHERE pau.usuario_id = :userId AND pau.id_recurso = bs.id " +
            "AND pau.tipo_recurso = 'BUILDER_SCREEN' AND pau.pode_visualizar = true) " +
            "OR EXISTS (SELECT 1 FROM permissao p WHERE p.id_recurso = bs.id AND p.tipo_recurso = 'BUILDER_SCREEN' " +
            "AND p.pode_visualizar = true AND p.perfil_id IN " +
            "(SELECT up.perfil_id FROM usuario_perfil up WHERE up.usuario_id = :userId))" +
            ") AND NOT EXISTS (SELECT 1 FROM restricao_especifica_usuario rsu " +
            "WHERE rsu.usuario_id = :userId AND rsu.id_recurso = bs.id AND rsu.tipo_recurso = 'BUILDER_SCREEN' AND rsu.negar_visualizar = true)",
            nativeQuery = true)
    Page<BuilderScreen> findVisibleScreensForUser(Pageable pageable, @Param("userId") Integer userId);

    /**
     * MÉTODO SEGURO: Busca telas que são componentes de um grupo específico, visíveis para o usuário.
     * Substitui o antigo e inseguro findComponentByGroup.
     */
    @Query("SELECT DISTINCT bs FROM BuilderScreen bs " +
            "LEFT JOIN PermissaoAdicionalUsuario pau ON pau.idRecurso = bs.id AND pau.tipoRecurso = 'BUILDER_SCREEN' AND pau.usuario.usuario = :userId AND pau.podeVisualizar = true " +
            "LEFT JOIN Permissao p ON p.idRecurso = bs.id AND p.tipoRecurso = 'BUILDER_SCREEN' AND p.podeVisualizar = true " +
            "LEFT JOIN UsuarioPerfil up ON p.perfil.id = up.id.perfilId AND up.id.usuarioId = :userId " +
            "LEFT JOIN RestricaoEspecificaUsuario rsu ON rsu.idRecurso = bs.id AND rsu.tipoRecurso = 'BUILDER_SCREEN' AND rsu.usuario.usuario = :userId AND rsu.negarVisualizar = true " +
            "WHERE bs.active = true AND bs.componentS = true AND bs.menuGroup.id = :groupId " +
            "AND (pau.id IS NOT NULL OR up.id IS NOT NULL) AND rsu.id IS NULL")
    Page<BuilderScreen> findVisibleComponentsByGroupForUser(Pageable pageable, @Param("groupId") Long groupId, @Param("userId") Integer userId);

    /**
     * MÉTODO SEGURO: Busca telas que possuem preview, visíveis para o usuário.
     * Substitui o antigo e inseguro buildingScreenWithPreview.
     */
    @Query("SELECT DISTINCT bs FROM BuilderScreen bs " +
            "LEFT JOIN PermissaoAdicionalUsuario pau ON pau.idRecurso = bs.id AND pau.tipoRecurso = 'BUILDER_SCREEN' AND pau.usuario.usuario = :userId AND pau.podeVisualizar = true " +
            "LEFT JOIN Permissao p ON p.idRecurso = bs.id AND p.tipoRecurso = 'BUILDER_SCREEN' AND p.podeVisualizar = true " +
            "LEFT JOIN UsuarioPerfil up ON p.perfil.id = up.id.perfilId AND up.id.usuarioId = :userId " +
            "LEFT JOIN RestricaoEspecificaUsuario rsu ON rsu.idRecurso = bs.id AND rsu.tipoRecurso = 'BUILDER_SCREEN' AND rsu.usuario.usuario = :userId AND rsu.negarVisualizar = true " +
            "WHERE bs.active = true AND bs.previewObjectText IS NOT NULL AND bs.previewObjectText <> '' " +
            "AND (pau.id IS NOT NULL OR up.id IS NOT NULL) AND rsu.id IS NULL")
    List<BuilderScreen> findVisibleScreensWithPreviewForUser(@Param("userId") Integer userId);

    @Query("SELECT bs FROM BuilderScreen bs WHERE bs.active = true AND bs.componentS = true AND bs.menuGroup IS NULL AND (" +
            "EXISTS (SELECT 1 FROM PermissaoAdicionalUsuario pau " +
            "        WHERE pau.usuario.usuario = :userId AND pau.idRecurso = bs.id " +
            "        AND pau.tipoRecurso = 'BUILDER_SCREEN' AND pau.podeVisualizar = true) " +
            "OR EXISTS (SELECT 1 FROM Permissao p " +
            "          WHERE p.idRecurso = bs.id AND p.tipoRecurso = 'BUILDER_SCREEN' " +
            "          AND p.podeVisualizar = true AND p.perfil.id IN " +
            "              (SELECT up.id.perfilId FROM UsuarioPerfil up WHERE up.id.usuarioId = :userId))" +
            ") " +
            "AND NOT EXISTS (SELECT 1 FROM RestricaoEspecificaUsuario rsu " +
            "              WHERE rsu.usuario.usuario = :userId AND rsu.idRecurso = bs.id " +
            "              AND rsu.tipoRecurso = 'BUILDER_SCREEN' AND rsu.negarVisualizar = true)")
    Page<BuilderScreen> findVisibleComponentsWithoutGroupForUser(Pageable pageable, @Param("userId") Integer userId);

}