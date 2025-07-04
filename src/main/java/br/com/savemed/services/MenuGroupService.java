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
        // Este método também pode ser otimizado para buscar pelo objeto, mas o ID funciona.
        return repository.findScreenByGroup(pageable, id);
    }

    public MenuGroup findById(Long id) {
        LOGGER.info("Group Menu find one!");
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public MenuGroup create(MenuGroup item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();
        LOGGER.info("Group Menu create one!");
        return repository.save(item);
    }

    /**
     * MÉTODO CORRIGIDO: Associa uma tela (BuilderScreen) a um grupo de menu (MenuGroup).
     */
    public BuilderScreen updateGroup(Long idGroup, Long idScreen) {
        // 1. Busca a tela que será atualizada
        BuilderScreen screenToUpdate = builderScreenRepository.findById(idScreen)
                .orElseThrow(() -> new ResourceNotFoundException("Tela não encontrada com o ID: " + idScreen));

        // 2. Busca a entidade do NOVO GRUPO que será associado
        MenuGroup newGroup = repository.findById(idGroup)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo de Menu não encontrado com o ID: " + idGroup));

        // 3. Define o objeto de relacionamento, em vez de apenas o ID
        screenToUpdate.setMenuGroup(newGroup);

        return builderScreenRepository.save(screenToUpdate);
    }

    /**
     * MÉTODO CORRIGIDO: Remove a associação de uma tela com seu grupo.
     */
    public void removeToGroup(Long idScreen) {
        // 1. Busca a tela
        BuilderScreen screenToUpdate = builderScreenRepository.findById(idScreen)
                .orElseThrow(() -> new ResourceNotFoundException("Tela não encontrada com o ID: " + idScreen));

        // 2. Define o objeto de relacionamento como null para remover a associação
        screenToUpdate.setMenuGroup(null);

        builderScreenRepository.save(screenToUpdate);
    }

    public MenuGroup update(MenuGroup item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();
        LOGGER.info("Group Menu update one!");

        MenuGroup old = repository.findById(item.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No Records"));

        old.setTitle(item.getTitle());
        old.setIcon(item.getIcon());
        old.setActive(item.isActive());

        return repository.save(item);
    }

    @Transactional
    public MenuGroup disableMenu(Long id) {
        // Esta lógica também poderia ser movida para o serviço, como fizemos com NavigationItemService,
        // mas a query @Modifying ainda funciona.
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