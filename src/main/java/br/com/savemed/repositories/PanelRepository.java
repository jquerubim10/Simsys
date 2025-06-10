package br.com.savemed.repositories;

import br.com.savemed.model.Panel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PanelRepository extends JpaRepository<Panel, Long> {

    @Modifying
    @Query("update Panel p set p.active = false where p.id = :id")
    void disablePainel(@Param("id") Long id);

    @Query("select f from Panel  f where f.active = true")
    Page<Panel> findAllFilter(Pageable pageable);
}
