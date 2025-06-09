package br.com.savemed.repositories.file;

import br.com.savemed.model.file.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDbRepository extends JpaRepository<FileDB, Long> {
}
