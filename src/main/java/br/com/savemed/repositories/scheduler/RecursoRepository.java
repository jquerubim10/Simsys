package br.com.savemed.repositories.scheduler;

import br.com.savemed.model.CentroCusto;
import br.com.savemed.model.Panel;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.savemed.CentroCustoSavemed;
import org.springframework.data.domain.Page;
import br.com.savemed.model.scheduler.Recurso;
import br.com.savemed.model.scheduler.PermissaoAgendamento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {


    @Query("select distinct cm from CentroCustoSavemed cm inner join PermissaoAgendamento pa on cm.centrocusto = pa.centroCustoID where pa.secretariaID = :id")
    Page<CentroCustoSavemed> findAllSetor(Pageable pageable, Long id);

    @Query("select r from Recurso r where (r.nome like %:text% or r.codigo like :text%) and ( r.centroCustoId = :id or r.centroCustoId is null) and r.tipoRecurso = %:tipo%")
    Page<Recurso> findAllBySql(Pageable pageable, String text, String tipo, Long id);
}
