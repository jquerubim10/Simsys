package br.com.savemed.services.scheduler;

import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.scheduler.Notificacao;
import br.com.savemed.repositories.scheduler.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.logging.Logger;

@Service
public class NotificacaoService {

    private final Logger logger = Logger.getLogger(NotificacaoService.class.getName());

    @Autowired
    NotificacaoRepository repository;

    public Page<Notificacao> findAll(Pageable pageable) {
        logger.info("Finding all Notificacao");
        return repository.findAll(pageable);
    }

    public Page<Notificacao> findAllBySql(Pageable pageable, QueryBody queryBody) {
        logger.info("Finding Notificacao by SQL");
        return repository.findAllBySql(pageable, queryBody); // Passa o QueryBody inteiro
    }

    public Notificacao findById(Long id) throws Exception {
        logger.info("Finding one Notificacao");
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public Notificacao create(Notificacao item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();
        logger.info("Creating one Notificacao");
        return repository.save(item);
    }

    public Notificacao update(Notificacao item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();
        logger.info("Updating one Notificacao");
        Notificacao old = repository.findById(item.getId()).orElseThrow(() -> new ResourceNotFoundException("No Records"));
        return repository.save(item);
    }

    public void delete(Long id) {
        logger.info("Deleting one Notificacao");
        Notificacao obj = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
        repository.delete(obj);
    }
}
