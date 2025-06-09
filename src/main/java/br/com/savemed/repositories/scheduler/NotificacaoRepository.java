package br.com.savemed.repositories.scheduler;

import br.com.savemed.model.scheduler.Notificacao;
import br.com.savemed.model.query.QueryBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    @Query("SELECT n FROM Notificacao n WHERE " +
            "(:#{#queryBody.whereValue} IS NULL OR " +
            "UPPER(n.meioEnvio) LIKE UPPER(CONCAT('%', :#{#queryBody.whereValue}, '%')) OR " +
            "UPPER(n.statusEnvio) LIKE UPPER(CONCAT('%', :#{#queryBody.whereValue}, '%'))) " +
            "AND (:#{#queryBody.operationType} IS NULL OR " +
            "n.tentativas = :#{#queryBody.valueLong})")
    Page<Notificacao> findAllBySql(Pageable pageable, QueryBody queryBody);
}