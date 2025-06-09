package br.com.savemed.repositories;

import br.com.savemed.model.auth.UserSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignRepository extends JpaRepository<UserSave, Integer> {
    UserSave findByLogon(String logon);
}
