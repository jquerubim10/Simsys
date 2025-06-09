package br.com.savemed.services.scheduler;


import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.scheduler.AgendamentoRecurso;
import br.com.savemed.repositories.scheduler.AgendamentoRecursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class AgendamentoRecursoService {

    private final Logger logger = Logger.getLogger(AgendamentoRecursoService.class.getName());

    @Autowired
    AgendamentoRecursoRepository repository;

    public Page<AgendamentoRecurso> findAll(Pageable pageable) {
        logger.info("find all scheduler resources");

        return repository.findAll(pageable);
    }

    public AgendamentoRecurso findById(Long id) throws Exception {
        logger.info("Finding one scheduler resources!");

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public Page<AgendamentoRecurso> findAllByAgendamento(Pageable pageable, Long id) {
        logger.info("Finding all Agendamento recursos!");
        return repository.findAllResources(pageable, id);
    }

    public List<AgendamentoRecurso> findAllBySchedulerWithoutPage(Long id) {
        logger.info("Finding all Agendamento recursos sem paginação!");
        return repository.findAllBySchedulerWithoutPage(id);
    }

    @Transactional
    public AgendamentoRecurso create(AgendamentoRecurso item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Creating one scheduler resources!");

        return repository.save(item);
    }

    @Transactional
    public AgendamentoRecurso update(AgendamentoRecurso item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();
        logger.info("Update one scheduler resources!");
        return repository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Deleting one scheduler resources!");

        AgendamentoRecurso old = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        repository.delete(old);
    }

    @Transactional
    public void deleteAllResources(Long id) {
        logger.info("Deleting all scheduler resources!");

        repository.deleteAllResources(id);

        logger.info("Success to delete all scheduler resources!");
    }
}
