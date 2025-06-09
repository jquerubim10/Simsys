package br.com.savemed.services.scheduler;

import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.enums.CanalType;
import br.com.savemed.model.enums.EntidadeType;
import br.com.savemed.model.message.Mensagem;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.savemed.MedicoSavemed;
import br.com.savemed.model.scheduler.*;
import br.com.savemed.repositories.message.MensagemRepository;
import br.com.savemed.repositories.scheduler.AgendamentoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class AgendamentoService {

    private final Logger logger = Logger.getLogger(AgendamentoService.class.getName());

    @Autowired
    AgendamentoRepository repository;

    @Autowired
    AgendamentoRecursoService agendamentoRecursoService;

    @Autowired
    EquipeAgendamentoService equipeAgendamentoService;

    @Autowired
    private ConfiguracaoNotificacaoService configService;

    public Page<Agendamento> findAll(Pageable pageable) {
        logger.info("find all scheduler");

        return repository.findAllWhere(pageable);
    }

    public Page<Agendamento> findAllSql(Pageable pageable, QueryBody body) {
        logger.info("find all scheduler filtered");

        return repository.findAllSql(pageable, body.getValueMedico(), body.getValueLong()   );
    }

    public Page<MedicoSavemed> findAllDoctors(Pageable pageable, Long id_logged, Long id_center) {
        logger.info("find all doctors");

        return repository.findAllDoctors(pageable, id_logged, id_center);
    }

    public Page<Convenio> findAllConvenios(Pageable pageable) {
        logger.info("find all doctors");

        return repository.findAllConvenios(pageable);
    }

    public Agendamento findById(Long id) throws Exception {
        logger.info("Finding one builder div!");

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    @Transactional
    public Agendamento create(Agendamento item) throws JsonProcessingException {
        ObjectMapper objectMapper   = new ObjectMapper();
        JsonNode jsonNode           = objectMapper.readTree(item.getActions());
        String resourcesList        = jsonNode.get("resources_list").toString();
        String teamList             = jsonNode.get("teams_list").toString();

        Agendamento scheduler       = repository.save(item);

        List<AgendamentoRecurso> agendamentoRecursos    = objectMapper.readValue(resourcesList, new TypeReference<>() {});
        List<EquipeAgendamento> equipeAgendamentos      = objectMapper.readValue(teamList, new TypeReference<>() {});

        agendamentoRecursos.forEach( resource -> {
            resource.setAgendamentoID(scheduler.getId());

            agendamentoRecursoService.create(resource);

            logger.info("Recurso Agendamento salvo com successo!!");
        });

        equipeAgendamentos.forEach(team -> {
            team.setAgendamentoID(scheduler.getId());

            equipeAgendamentoService.create(team);
            logger.info("Equipe Agendamento salvo com successo!!");
        });


        logger.info("Creating Scheduler success!");

        return scheduler;
    }

    @Transactional
    public Agendamento updateStatus(QueryBody item) {
        Agendamento old = repository.findById(item.getValueLong()).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        if (Objects.isNull(old)) throw new RequiredObjectIsNullException();

        logger.info("Update status agendamento!");

        old.setStatus(item.getWhereValue());
        old.setColor(item.getSecondWhereValue());

        return repository.save(old);
    }

    @Transactional
    public Agendamento update(Agendamento scheduler) throws JsonProcessingException {
        ObjectMapper objectMapper   = new ObjectMapper();
        JsonNode jsonNode           = objectMapper.readTree(scheduler.getActions());
        String resourcesList        = jsonNode.get("resources_list").toString();
        String teamList             = jsonNode.get("teams_list").toString();

        agendamentoRecursoService.deleteAllResources(scheduler.getId());
        equipeAgendamentoService.deleteAllTeam(scheduler.getId());

        logger.info("Agendamentos Recursos e Equipe apagados da base com successo!!");

        List<AgendamentoRecurso> agendamentoRecursos    = objectMapper.readValue(resourcesList, new TypeReference<>() {});
        List<EquipeAgendamento> equipeAgendamentos      = objectMapper.readValue(teamList, new TypeReference<>() {});

        agendamentoRecursos.forEach( resource -> {
            resource.setAgendamentoID(scheduler.getId());

            agendamentoRecursoService.create(resource);

            logger.info("Recurso Agendamento salvo com successo!!");
        });

        equipeAgendamentos.forEach(team -> {
            team.setAgendamentoID(scheduler.getId());

            equipeAgendamentoService.create(team);
            logger.info("Equipe Agendamento salvo com successo!!");
        });

        logger.info("Update Agendamento!");
        return repository.save(scheduler);
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Deleting one builder div!");

        Agendamento old = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        repository.delete(old);
    }
    public List<Agendamento> findAgendamentosParaNotificar(CanalType canal) {
        ConfiguracaoNotificacao config = configService.getConfiguracaoAtiva();
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limite = agora.plusHours(config.getHorasAntesAgendamento());

        return repository.findAgendamentosParaNotificar(
                agora,
                limite,
                CanalType.WHATSAPP.equals(canal),
                List.of("cancelada_pelo_paciente", "cancelada_pelo_profissional")
        );
    }
}
