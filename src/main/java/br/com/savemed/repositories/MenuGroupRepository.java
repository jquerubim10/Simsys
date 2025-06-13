package br.com.savemed.repositories;

import br.com.savemed.model.builder.BuilderScreen;
import br.com.savemed.model.builder.MenuGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuGroupRepository extends JpaRepository<MenuGroup, Long> {

    @Modifying
    @Query("update MenuGroup mg set mg.active = false where mg.id = :id")
    void disableGroup(@Param("id") Long id);

    /**
     * MÉTODO CORRIGIDO: Busca telas (BuilderScreen) que pertencem a um grupo específico.
     * A query agora usa o relacionamento de objeto 'menuGroup' em vez do campo 'groupId'.
     */
    @Query("select bs from BuilderScreen bs where bs.menuGroup.id = :id")
    Page<BuilderScreen> findScreenByGroup(Pageable pageable, @Param("id") Long id);
}