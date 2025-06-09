package br.com.savemed.repositories.file;

import br.com.savemed.model.file.PfxUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PfxUserRepository extends JpaRepository<PfxUser, Long> {

    @Query("select f from PfxUser  f where f.username = :id")
    PfxUser findByPfx(String id);
}
