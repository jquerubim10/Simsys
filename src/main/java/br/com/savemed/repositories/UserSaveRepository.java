package br.com.savemed.repositories;

import br.com.savemed.model.auth.UserSave;
import br.com.savemed.model.ws.InformacaoPaciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSaveRepository extends JpaRepository<UserSave, Integer> {
    UserSave findByLogon(String logon);

    @Query("select f from UserSave f where f.usuario = :id")
    UserSave findById(Long id);

    @Query("SELECT i FROM InformacaoPaciente i")
    Page<InformacaoPaciente> findAllPatients(Pageable pageable);
}
