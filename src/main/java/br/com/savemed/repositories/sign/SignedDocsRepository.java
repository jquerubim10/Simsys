package br.com.savemed.repositories.sign;

import br.com.savemed.model.file.SignedDocs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignedDocsRepository extends JpaRepository<SignedDocs, Long> {


}
