package br.com.savemed.services;


import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.builder.BuilderDiv;
import br.com.savemed.model.builder.BuilderField;
import br.com.savemed.model.builder.BuilderScreen;
import br.com.savemed.repositories.BuilderDivRepository;
import br.com.savemed.repositories.BuilderFieldRepository;
import br.com.savemed.repositories.BuilderScreenRepository;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class BuilderScreenService {

    @Autowired
    BuilderScreenRepository repository;

    @Autowired
    BuilderDivRepository builderDivRepository;

    @Autowired
    BuilderFieldRepository builderFieldRepository;

    private final Logger logger = Logger.getLogger(BuilderScreenService.class.getName());

    public Page<BuilderScreen> findAll(Pageable pageable) {
        logger.info("find all builder screens");

        return repository.findAllFilter(pageable);
    }

    public Page<BuilderScreen> findAllComponent(Pageable pageable) {
        logger.info("find all builder screens");

        return repository.findAllComponent(pageable);
    }

    public Page<BuilderScreen> findComponentByGroup(Pageable pageable, Long id) {
        logger.info("find all component by group");
        return repository.findComponentByGroup(pageable, id);
    }

    public BuilderScreen findById(Long id) throws Exception {
        logger.info("Finding one builder screen!");

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public List<BuilderScreen> buildingScreenWithPreview() {
        logger.info("Get all Screen With Preview");
        return repository.buildingScreenWithPreview();
    }

    public BuilderScreen buildingScreen(Long id) throws Exception {
        Gson ow = new Gson();

        logger.info("Building screen!");
        BuilderScreen screen = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
        List<BuilderDiv> allDivs = List.of();

        if (Objects.nonNull(screen)) {
            allDivs = builderDivRepository.findDivToBuilding(screen.getId());

            allDivs.forEach(
                    (div) -> {
                        List<BuilderField> fields = builderFieldRepository.buildingScreenFieldsExcludePreview(div.getId());
                        div.setFieldJson(ow.toJson(fields));
                    }
            );
        }

        screen.setDivJson(ow.toJson(allDivs));
        return screen;
    }

    public BuilderScreen create(BuilderScreen item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Creating one builder screen!");

        return repository.save(item);
    }

    public BuilderScreen update(BuilderScreen item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Update one builder screen!");

        BuilderScreen old = repository.findById(item.getId()).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        old.setTitle(item.getTitle());
        old.setIcon(item.getIcon());
        old.setTableName(item.getTableName());

        return repository.save(item);
    }

    @Transactional
    public BuilderScreen disableBuilderScreen(Long id) {
        logger.info("Disabling one builder screen!");
        repository.disableMenu(id);

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public void delete(Long id) {
        logger.info("Deleting one builder screen!");

        BuilderScreen old = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        repository.delete(old);
    }
}
