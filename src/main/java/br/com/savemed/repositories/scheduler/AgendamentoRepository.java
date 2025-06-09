package br.com.savemed.repositories.scheduler;

import br.com.savemed.model.savemed.MedicoSavemed;
import br.com.savemed.model.scheduler.Agendamento;
import br.com.savemed.model.scheduler.Convenio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {



    @Query("select distinct cm from MedicoSavemed cm " +
            "inner join PermissaoAgendamento pa on cm.medico = pa.medicoID " +
            "where pa.secretariaID = :id_logged and pa.centroCustoID = :id_center")
    Page<MedicoSavemed> findAllDoctors(Pageable pageable, Long id_logged, Long id_center);

    @Query("select a from Agendamento a WHERE a.status NOT IN ('cancelada_pelo_paciente', 'cancelada_pelo_profissional')")
    Page<Agendamento> findAllWhere(Pageable pageable);


    @Query("select c from Convenio c")
    Page<Convenio> findAllConvenios(Pageable pageable);

    @Query("select a from Agendamento a where a.medicoID = :id_medico and a.centroCustoID = :id_centro and a.status NOT IN ('cancelada_pelo_paciente', 'cancelada_pelo_profissional')")
    Page<Agendamento> findAllSql(Pageable pageable, Long id_medico, Long id_centro);

    @Query("SELECT a FROM Agendamento a " +
            "WHERE a.horaInicio BETWEEN :inicio AND :fim " +
            "AND a.status NOT IN :statusExcluidos")
    List<Agendamento> findByHoraInicioBetweenAndStatusNot(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("statusExcluidos") List<String> statusExcluidos
    );
/*
    @Query("SELECT a FROM Agendamento a " +
            "WHERE a.horaInicio BETWEEN :inicio AND :fim " +
            "AND a.status NOT IN :statusExcluidos " +
            "AND a.whatsapp = :whatsapp " +
            "AND NOT EXISTS ( " +
            "   SELECT n FROM Notificacao n " +
            "   WHERE n.agendamento = a " +
            "   AND n.statusEnvio NOT IN('Falhou','FALHA')  " +
            ")")
    List<Agendamento> findAgendamentosParaNotificar(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("whatsapp") boolean  whatsapp,
            @Param("statusExcluidos") List<String> statusExcluidos
    );*/
    @Query("SELECT a FROM Agendamento a " +
            "WHERE a.horaInicio BETWEEN :inicio AND :fim " +
            "AND a.status NOT IN :statusExcluidos " +
            "AND a.whatsapp = :whatsapp " +
            "AND NOT EXISTS ( " +
            "   SELECT m FROM Mensagem m " +
            "   WHERE m.entidadeRef.type = br.com.savemed.model.enums.EntidadeType.AGENDAMENTO " +
            "   AND m.entidadeRef.id = a.id " +
            "   AND m.status IN (br.com.savemed.model.enums.StatusMensagem.ENVIADO) " +
            ")")
    List<Agendamento> findAgendamentosParaNotificar(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("whatsapp") boolean whatsapp,
            @Param("statusExcluidos") List<String> statusExcluidos
    );
}
