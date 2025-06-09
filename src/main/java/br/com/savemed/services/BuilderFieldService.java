package br.com.savemed.services;

import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.builder.BuilderField;
import br.com.savemed.repositories.BuilderDivRepository;
import br.com.savemed.repositories.BuilderFieldRepository;
import jakarta.persistence.Column;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.logging.Logger;

@Service
public class BuilderFieldService {

    @Autowired
    BuilderFieldRepository repository;
    private final Logger logger = Logger.getLogger(BuilderFieldService.class.getName());

    public Page<BuilderField> findAll(Pageable pageable) {
        logger.info("find all builder fields");

        return repository.findAllFilter(pageable);
    }

    public Page<BuilderField> findAllFilterScreen(Pageable pageable, Long id) {
        logger.info("find all builder fields by Screen");

        return repository.findAllFilterScreen(pageable, id);
    }

    public Page<BuilderField> findFieldSearch(Pageable pageable, Long id) {
        logger.info("find all builder fields searchable");

        return repository.findFieldSearch(pageable, id);
    }

    public Page<BuilderField> findAllFilterDiv(Pageable pageable, Long id) {
        logger.info("find all builder fields by div");

        return repository.findAllFilterDiv(pageable, id);
    }

    public BuilderField findById(Long id) throws Exception {
        logger.info("Finding one builder field!");

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public Page<BuilderField> findIdentityList(Pageable pageable, Long id) {
        logger.info("find all builder fields identity");

        return repository.findIdentityList(pageable, id);
    }

    public BuilderField create(BuilderField item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Creating one builder field!");

        return repository.save(item);
    }

    public BuilderField update(BuilderField item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        logger.info("Update one builder field!");

        BuilderField old = repository.findById(item.getId()).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        old.setPlaceholder(item.getPlaceholder());
        old.setType(item.getType());
        old.setLabel(item.getLabel());
        old.setFormControlName(item.getFormControlName());
        old.setLines(item.getLines());
        old.setValue(item.getValue());
        old.setCss(item.getCss());
        old.setClassName(item.getClassName());
        old.setColumnName(item.getColumnName());
        old.setMask(item.getMask());

        old.setInfoSensitive(item.isInfoSensitive());
        old.setSearchable(item.isSearchable());
        old.setEditable(item.isEditable());
        old.setVisible(item.isVisible());

        old.setValidatorRequired(item.isValidatorRequired());
        old.setValidatorMin(item.getValidatorMin());
        old.setValidatorMax(item.getValidatorMax());
        old.setValidatorMessage(item.getValidatorMessage());

        old.setActive(item.isActive());

        return repository.save(item);
    }

    @Transactional
    public BuilderField disableDiv(Long id) {
        logger.info("Disabling one builder field!");
        repository.disableField(id);

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public void delete(Long id) {
        logger.info("Deleting one builder field!");

        BuilderField old = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));

        repository.delete(old);
    }
}
