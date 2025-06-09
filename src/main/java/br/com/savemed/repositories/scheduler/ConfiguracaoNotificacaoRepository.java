package br.com.savemed.repositories.scheduler;

import br.com.savemed.model.enums.CanalType;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.scheduler.ConfiguracaoNotificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracaoNotificacaoRepository extends JpaRepository<ConfiguracaoNotificacao, Long> {

    @Query("SELECT c FROM ConfiguracaoNotificacao c WHERE " +
            "( :#{#queryBody.whereValue} IS NULL OR " +
            "  UPPER(CAST(c.tipoEnvio AS string)) LIKE UPPER(CONCAT('%', :#{#queryBody.whereValue}, '%')) ) " +
            "AND ( :#{#queryBody.operationType} IS NULL OR " +
            "      c.horasAntesAgendamento = :#{#queryBody.valueLong} )")
    Page<ConfiguracaoNotificacao> findAllBySql(
            Pageable pageable,
            @Param("queryBody") QueryBody queryBody
    );
    Optional<ConfiguracaoNotificacao> findFirstByTipoEnvio(CanalType tipoEnvio);
    Optional<ConfiguracaoNotificacao> findByTipoEnvio(CanalType tipoEnvio);
    Optional<ConfiguracaoNotificacao> findTopByOrderByConfigIDDesc();
}