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

    @Modifying
    @Query("update BuilderScreen f set f.active = false where f.id = :id")
    void disableMenu(@Param("id") Long id);

    @Query("select f from BuilderScreen  f where f.active = true")
    Page<BuilderScreen> findAllFilter(Pageable pageable);

    @Query("select f from BuilderScreen  f where f.active = true and f.componentS = true and f.groupId is null")
    Page<BuilderScreen> findAllComponent(Pageable pageable);

    @Query("select f from BuilderScreen  f where f.active = true and f.componentS = true and f.groupId = :id")
    Page<BuilderScreen> findComponentByGroup(Pageable pageable,@Param("id") Long id);

    @Query("select f from BuilderScreen f where f.active = true and f.previewObjectText != ''")
    List<BuilderScreen> buildingScreenWithPreview();
}
