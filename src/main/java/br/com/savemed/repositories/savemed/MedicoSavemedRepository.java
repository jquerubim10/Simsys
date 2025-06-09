package br.com.savemed.repositories.savemed;

import br.com.savemed.model.savemed.MedicoSavemed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoSavemedRepository extends JpaRepository<MedicoSavemed, Long> {
}
