package br.com.savemed.services;


import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.builder.BuilderScreen;
import br.com.savemed.model.builder.MenuGroup;
import br.com.savemed.repositories.BuilderScreenRepository;
import br.com.savemed.repositories.MenuGroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.logging.Logger;

@Service
public class MenuGroupService {
    private static final Logger LOGGER = Logger.getLogger(MenuGroupService.class.getName());

    @Autowired
    MenuGroupRepository repository;

    @Autowired
    BuilderScreenRepository builderScreenRepository;

    public Page<MenuGroup> findAll(Pageable pageable) {
        LOGGER.info("Group Menu find all");

        return repository.findAll(pageable);
    }

    public Page<BuilderScreen> findAllScreenByGroup(Pageable pageable, Long id) {
        LOGGER.info("find all screen by group");
        return repository.findScreenByGroup(pageable, id);
    }

    public MenuGroup findById(Long id) throws Exception {
        LOGGER.info("Group Menu find one!");

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public MenuGroup create(MenuGroup item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        LOGGER.info("Group Menu create one!");

        return repository.save(item);
    }

    public BuilderScreen updateGroup(Long idGroup, Long idScreen) {
        BuilderScreen old = builderScreenRepository.findById(idScreen).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        old.setGroupId(idGroup);
        return builderScreenRepository.save(old);
    }

    public void removeToGroup(Long idScreen) {
        BuilderScreen old = builderScreenRepository.findById(idScreen).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        old.setGroupId(null);
        builderScreenRepository.save(old);
    }

    public MenuGroup update(MenuGroup item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        LOGGER.info("Group Menu update one!");


        MenuGroup old = repository.findById(item.getId()).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        old.setTitle(item.getTitle());
        old.setIcon(item.getIcon());
        old.setActive(item.isActive());

        return repository.save(item);
    }

    @Transactional
    public MenuGroup disableMenu(Long id) {
        LOGGER.info("Group Menu disabled one!");
        repository.disableGroup(id);

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public void delete(Long id) {
        LOGGER.info("Group Menu delete one!");

        MenuGroup old = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        repository.delete(old);
    }
}
