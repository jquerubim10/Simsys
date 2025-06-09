package br.com.savemed.repositories;

import br.com.savemed.model.FuseNavigationItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FuseNavigationItemRepository extends JpaRepository<FuseNavigationItem, Long> {

    @Modifying
    @Query("update FuseNavigationItem f set f.active = false where f.id = :id")
    void disableMenu(@Param("id") Long id);

    @Query("select f from FuseNavigationItem f where f.hidden = false order by f.meta asc")
    Page<FuseNavigationItem> findAllFilter(Pageable pageable);

    @Query("select f from FuseNavigationItem f where f.idSidebarMenu = :id and f.hidden = false")
    Page<FuseNavigationItem> findAllSidebarChildren(Pageable pageable, @Param("id") Long id);
}
