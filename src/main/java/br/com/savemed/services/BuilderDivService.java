package br.com.savemed.services;

import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.builder.BuilderDiv;
import br.com.savemed.repositories.BuilderDivRepository;
import br.com.savemed.repositories.BuilderScreenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.logging.Logger;

@Service
public class BuilderDivService {

    @Autowired
    BuilderDivRepository repository;
    private final Logger logger = Logger.getLogger(BuilderDivService.class.getName());

    public Page<BuilderDiv> findAll(Pageable pageable) {
        logger.info("find all builder divs");

        return repository.findAllFilter(pageable);
    }

    public Page<BuilderDiv> findAllByDiv(Pageable pageable, Long id) {
        logger.info("find all builder divs by screen");

        return repository.findAllDivScreen(pageable, id);
    }

    public BuilderDiv findById(Long id) throws Exception {
        logger.info("Finding one builder div!");

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public BuilderDiv create(BuilderDiv item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Creating one builder div!");

        return repository.save(item);
    }

    public BuilderDiv update(BuilderDiv item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Update one builder div!");

        BuilderDiv old = repository.findById(item.getId()).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        old.setTitle(item.getTitle());

        return repository.save(item);
    }

    @Transactional
    public BuilderDiv disableDiv(Long id) {
        logger.info("Disabling one builder div!");
        repository.disableDiv(id);

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public void delete(Long id) {
        logger.info("Deleting one builder div!");

        BuilderDiv old = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        repository.delete(old);
    }
}
