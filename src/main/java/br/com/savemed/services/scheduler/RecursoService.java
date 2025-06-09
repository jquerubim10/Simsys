package br.com.savemed.services.scheduler;

import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.CentroCusto;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.savemed.CentroCustoSavemed;
import br.com.savemed.model.scheduler.Recurso;
import br.com.savemed.repositories.scheduler.RecursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.logging.Logger;

@Service
public class RecursoService {

    private final Logger logger = Logger.getLogger(RecursoService.class.getName());

    @Autowired
    RecursoRepository repository;

    public Page<Recurso> findAll(Pageable pageable) {
        logger.info("find all Recursos");

        return repository.findAll(pageable);
    }

    public Page<Recurso> findAllBySql(Pageable pageable, QueryBody queryBody) {
        logger.info("find all Recursos by SQL");

        return repository.findAllBySql(pageable, queryBody.getWhereValue(), queryBody.getOperationType() , queryBody.getValueLong());
    }

    public Page<CentroCustoSavemed> findAllSetor(Pageable pageable, Long id) {
        logger.info("find all Setor");

        return repository.findAllSetor(pageable, id);
    }

    public Recurso findById(Long id) throws Exception {
        logger.info("Finding one Recurso!");

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public Recurso create(Recurso item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Creating one Recurso!");

        return repository.save(item);
    }

    public Recurso update(Recurso item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Update one Recurso!");

        Recurso old = repository.findById(item.getId()).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        return repository.save(item);
    }

    public void delete(Long id) {
        logger.info("Deleting one Recurso!");

        Recurso obj = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        repository.delete(obj);
    }
}
