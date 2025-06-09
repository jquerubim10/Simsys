package br.com.savemed.repositories;

import br.com.savemed.model.builder.BuilderField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuilderFieldRepository extends JpaRepository<BuilderField, Long> {
    @Modifying
    @Query("update BuilderField f set f.active = false where f.id = :id")
    void disableField(@Param("id") Long id);

    @Query("select f from BuilderField  f where f.active = true")
    Page<BuilderField> findAllFilter(Pageable pageable);

    @Query("select f from BuilderField  f where f.active = true and f.screenId = :id")
    Page<BuilderField> findAllFilterScreen(Pageable pageable, Long id);

    @Query("select f from BuilderField  f where f.active = true and f.divId = :id")
    Page<BuilderField> findAllFilterDiv(Pageable pageable, Long id);

    @Query("select f from BuilderField  f where f.active = true and f.divId = :id")
    List<BuilderField> buildingScreenFields(Long id);

    @Query("select f from BuilderField  f where f.active = true and f.previewOnly = false and f.divId = :id")
    List<BuilderField> buildingScreenFieldsExcludePreview(Long id);

    @Query("select f from BuilderField  f where f.searchable = true and f.screenId = :id")
    Page<BuilderField> findFieldSearch(Pageable pageable, Long id);

    @Query("select f from BuilderField f where f.screenId = :id and f.type = 'identity'")
    Page<BuilderField> findIdentityList(Pageable pageable, Long id);
}
