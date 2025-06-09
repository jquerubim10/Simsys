package br.com.savemed.repositories.scheduler;

import br.com.savemed.model.scheduler.AgendamentoRecurso;
import br.com.savemed.model.scheduler.EquipeAgendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipeAgendamentoRepository extends JpaRepository<EquipeAgendamento, Long> {

    @Query("select a.funcao, a.medicoID, b.nome from EquipeAgendamento a inner join MedicoSavemed b on a.medicoID = b.medico where a.agendamentoID = :id")
    Page<EquipeAgendamento> findAllTeamsByScheduler(Pageable pageable, Long id);

    @Modifying
    @Query("DELETE FROM EquipeAgendamento WHERE agendamentoID = :id")
    void deleteAllResources(Long id);
}
