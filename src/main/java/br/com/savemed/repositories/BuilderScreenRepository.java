package br.com.savemed.repositories;

import br.com.savemed.model.builder.BuilderScreen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuilderScreenRepository extends JpaRepository<BuilderScreen, Long> {

    /**
     * MÉTODO SEGURO E CORRIGIDO: Usa LEFT JOINs diretos para máxima compatibilidade.
     */
    @Query("SELECT DISTINCT bs FROM BuilderScreen bs " +
            "LEFT JOIN PermissaoAdicionalUsuario pau ON pau.idRecurso = bs.id AND pau.tipoRecurso = 'BUILDER_SCREEN' AND pau.usuario.usuario = :userId AND pau.podeVisualizar = true " +
            "LEFT JOIN Permissao p ON p.idRecurso = bs.id AND p.tipoRecurso = 'BUILDER_SCREEN' AND p.podeVisualizar = true " +
            "LEFT JOIN UsuarioPerfil up ON up.id.perfilId = p.perfil.id AND up.id.usuarioId = :userId " +
            "LEFT JOIN RestricaoEspecificaUsuario rsu ON rsu.idRecurso = bs.id AND rsu.tipoRecurso = 'BUILDER_SCREEN' AND rsu.usuario.usuario = :userId AND rsu.negarVisualizar = true " +
            "WHERE bs.active = true " +
            "AND (pau.id IS NOT NULL OR up.id IS NOT NULL) " + // Condição de permissão: ou a direta (pau) ou a de perfil (up) foi encontrada
            "AND rsu.id IS NULL") // Condição de segurança: e nenhuma restrição (rsu) foi encontrada
    Page<BuilderScreen> findVisibleScreensForUser(Pageable pageable, @Param("userId") Integer userId);

    @Query("SELECT DISTINCT bs FROM BuilderScreen bs " +
            "LEFT JOIN PermissaoAdicionalUsuario pau ON pau.idRecurso = bs.id AND pau.tipoRecurso = 'BUILDER_SCREEN' AND pau.usuario.usuario = :userId AND pau.podeVisualizar = true " +
            "LEFT JOIN Permissao p ON p.idRecurso = bs.id AND p.tipoRecurso = 'BUILDER_SCREEN' AND p.podeVisualizar = true " +
            "LEFT JOIN UsuarioPerfil up ON up.id.perfilId = p.perfil.id AND up.id.usuarioId = :userId " +
            "LEFT JOIN RestricaoEspecificaUsuario rsu ON rsu.idRecurso = bs.id AND rsu.tipoRecurso = 'BUILDER_SCREEN' AND rsu.usuario.usuario = :userId AND rsu.negarVisualizar = true " +
            "WHERE bs.active = true AND bs.componentS = true AND bs.menuGroup IS NULL " +
            "AND (pau.id IS NOT NULL OR up.id IS NOT NULL) " +
            "AND rsu.id IS NULL")
    Page<BuilderScreen> findVisibleComponentsWithoutGroupForUser(Pageable pageable, @Param("userId") Integer userId);

    @Query("SELECT DISTINCT bs FROM BuilderScreen bs " +
            "LEFT JOIN FETCH bs.menuGroup " + // JOIN FETCH aqui é seguro pois é to-one
            "LEFT JOIN PermissaoAdicionalUsuario pau ON pau.idRecurso = bs.id AND pau.tipoRecurso = 'BUILDER_SCREEN' AND pau.usuario.usuario = :userId AND pau.podeVisualizar = true " +
            "LEFT JOIN Permissao p ON p.idRecurso = bs.id AND p.tipoRecurso = 'BUILDER_SCREEN' AND p.podeVisualizar = true " +
            "LEFT JOIN UsuarioPerfil up ON up.id.perfilId = p.perfil.id AND up.id.usuarioId = :userId " +
            "LEFT JOIN RestricaoEspecificaUsuario rsu ON rsu.idRecurso = bs.id AND rsu.tipoRecurso = 'BUILDER_SCREEN' AND rsu.usuario.usuario = :userId AND rsu.negarVisualizar = true " +
            "WHERE bs.active = true AND bs.componentS = true AND bs.menuGroup.id = :groupId " +
            "AND (pau.id IS NOT NULL OR up.id IS NOT NULL) " +
            "AND rsu.id IS NULL")
    Page<BuilderScreen> findVisibleComponentsByGroupForUser(Pageable pageable, @Param("groupId") Long groupId, @Param("userId") Integer userId);

    @Query("SELECT DISTINCT bs FROM BuilderScreen bs " +
            "LEFT JOIN PermissaoAdicionalUsuario pau ON pau.idRecurso = bs.id AND pau.tipoRecurso = 'BUILDER_SCREEN' AND pau.usuario.usuario = :userId AND pau.podeVisualizar = true " +
            "LEFT JOIN Permissao p ON p.idRecurso = bs.id AND p.tipoRecurso = 'BUILDER_SCREEN' AND p.podeVisualizar = true " +
            "LEFT JOIN UsuarioPerfil up ON up.id.perfilId = p.perfil.id AND up.id.usuarioId = :userId " +
            "LEFT JOIN RestricaoEspecificaUsuario rsu ON rsu.idRecurso = bs.id AND rsu.tipoRecurso = 'BUILDER_SCREEN' AND rsu.usuario.usuario = :userId AND rsu.negarVisualizar = true " +
            "WHERE bs.active = true AND bs.previewObjectText IS NOT NULL AND bs.previewObjectText <> '' " +
            "AND (pau.id IS NOT NULL OR up.id IS NOT NULL) " +
            "AND rsu.id IS NULL")
    List<BuilderScreen> findVisibleScreensWithPreviewForUser(@Param("userId") Integer userId);

    @Modifying
    @Query("update BuilderScreen f set f.active = false where f.id = :id")
    void disableMenu(@Param("id") Long id);

}