package br.com.savemed.repositories.savemed;

import br.com.savemed.model.savemed.CentroCustoSavemed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentroCustoSavemedRepository extends JpaRepository<CentroCustoSavemed, Long> {
}
