package br.com.savemed.repositories.scheduler;

import br.com.savemed.model.scheduler.Agendamento;
import br.com.savemed.model.scheduler.AgendamentoRecurso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendamentoRecursoRepository extends JpaRepository<AgendamentoRecurso, Long> {
    @Query("select b.tipoRecurso, b.codigo, b.nome, a.recursoID, a.quantidade from AgendamentoRecurso a inner join Recurso b on a.recursoID = b.id where a.agendamentoID = :id")
    Page<AgendamentoRecurso> findAllResources(Pageable pageable, Long id);

    @Query("select b.tipoRecurso, b.codigo, b.nome, a.recursoID, a.quantidade from AgendamentoRecurso a inner join Recurso b on a.recursoID = b.id where a.agendamentoID = :id")
    List<AgendamentoRecurso> findAllBySchedulerWithoutPage(Long id);

    @Modifying
    @Query("DELETE FROM AgendamentoRecurso WHERE agendamentoID = :id")
    void deleteAllResources(Long id);

}
