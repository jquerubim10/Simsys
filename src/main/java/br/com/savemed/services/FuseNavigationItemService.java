package br.com.savemed.services;

import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.FuseNavigationItem;
import br.com.savemed.repositories.FuseNavigationItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class FuseNavigationItemService {

    private Logger logger = Logger.getLogger(FuseNavigationItemService.class.getName());

    @Autowired
    FuseNavigationItemRepository repository;

    public Page<FuseNavigationItem> findAll(Pageable pageable) {
        logger.info("find all menus");

        return repository.findAll(pageable);
    }

    public Page<FuseNavigationItem> findAllSidebarChildren(Pageable pageable, Long id) {
        logger.info("find all sidebar childrens");

        return repository.findAllSidebarChildren(pageable, id);
    }

    public FuseNavigationItem findById(Long id) throws Exception {
        logger.info("Finding one menu!");

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public FuseNavigationItem create(FuseNavigationItem item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Creating one menu!");

        return repository.save(item);
    }

    public FuseNavigationItem update(FuseNavigationItem item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Update one menu!");

        FuseNavigationItem old = repository.findById(item.getId()).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        old.setTitle(item.getTitle());
        old.setIdName(item.getIdName());
        old.setLink(item.getLink());
        old.setTooltip(item.getTooltip());
        old.setType(item.getType());
        old.setIcon(item.getIcon());

        return repository.save(item);
    }

    @Transactional
    public FuseNavigationItem disableMenu(Long id) {
        logger.info("Disabling one menu!");
        repository.disableMenu(id);

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public void delete(Long id) {
        logger.info("Deleting one menu!");

        FuseNavigationItem old = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        repository.delete(old);
    }
}
