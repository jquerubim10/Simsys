package br.com.savemed.services.scheduler;

import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.scheduler.AgendamentoRecurso;
import br.com.savemed.model.scheduler.EquipeAgendamento;
import br.com.savemed.repositories.scheduler.EquipeAgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.logging.Logger;

@Service
public class EquipeAgendamentoService {
    private final Logger logger = Logger.getLogger(EquipeAgendamentoService.class.getName());

    @Autowired
    EquipeAgendamentoRepository repository;

    public Page<EquipeAgendamento> findAll(Pageable pageable) {
        logger.info("find all equipe de agendamento");

        return repository.findAll(pageable);
    }

    public EquipeAgendamento findById(Long id) throws Exception {
        logger.info("Finding one equipe agendamento!");

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public Page<EquipeAgendamento> findAllTeamsByScheduler(Pageable pageable, Long id) {
        logger.info("Finding all equipe agendamento by scheduler!");
        return repository.findAllTeamsByScheduler(pageable, id);
    }

    public EquipeAgendamento create(EquipeAgendamento item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Creating one Equipe Agendamento!");

        return repository.save(item);
    }

    public EquipeAgendamento update(EquipeAgendamento item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();
        logger.info("Update one Equipe Agendamento!");
        return repository.save(item);
    }

    public void deleteAllTeam(Long id) {
        logger.info("Deleting all Equipe Agendamento!");

        repository.deleteAllResources(id);
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Deleting one Equipe Agendamento!");

        EquipeAgendamento old = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        repository.delete(old);
    }
}
