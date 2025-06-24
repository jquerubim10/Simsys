package br.com.savemed.repositories.savemed;

import br.com.savemed.model.savemed.LeitoUnidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeitoUnidadeRepository extends JpaRepository<LeitoUnidade, Integer> {
}
