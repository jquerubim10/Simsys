package br.com.savemed.repositories;

import br.com.savemed.model.builder.BuilderDiv;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuilderDivRepository extends JpaRepository<BuilderDiv, Long> {
    @Modifying
    @Query("update BuilderDiv f set f.active = false where f.id = :id")
    void disableDiv(@Param("id") Long id);

    @Query("select f from BuilderDiv  f where f.active = true")
    Page<BuilderDiv> findAllFilter(Pageable pageable);

    @Query("select f from BuilderDiv  f where f.active = true and f.screenId = :id")
    Page<BuilderDiv> findAllDivScreen(Pageable pageable, Long id);

    @Query("select f from BuilderDiv  f where f.active = true and f.screenId = :id")
    List<BuilderDiv> findDivToBuilding(Long id);
}
