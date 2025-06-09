package br.com.savemed.services;

import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.Panel;
import br.com.savemed.repositories.PanelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.logging.Logger;

@Service
public class PanelService {

    private Logger logger = Logger.getLogger(PanelService.class.getName());

    @Autowired
    PanelRepository repository;

    public Page<Panel> findAll(Pageable pageable) {
        logger.info("find all panels");

        return repository.findAllFilter(pageable);
    }

    public Panel findById(Long id) throws Exception {
        logger.info("Finding one panel!");

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public Panel create(Panel item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Creating one panel!");

        return repository.save(item);
    }

    public Panel update(Panel item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Update one panel!");

        Panel old = repository.findById(item.getId()).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        old.setTitle(item.getTitle());
        old.setIdName(item.getIdName());
        old.setDescription(item.getDescription());
        old.setUrl(item.getUrl());
        old.setIcon(item.getIcon());

        return repository.save(item);
    }

    @Transactional
    public Panel disablePanel(Long id) {
        logger.info("Disabling one panel!");
        repository.disablePainel(id);

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public void delete(Long id) {
        logger.info("Deleting one panel!");

        Panel old = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        repository.delete(old);
    }
}
