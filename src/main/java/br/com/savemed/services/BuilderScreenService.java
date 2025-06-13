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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class BuilderScreenService {

    @Autowired
    private BuilderScreenRepository repository;

    @Autowired
    private BuilderDivRepository builderDivRepository;

    @Autowired
    private BuilderFieldRepository builderFieldRepository;

    @Autowired
    private PermissionService permissionService; // Injetando o serviço de permissão

    private final Logger logger = Logger.getLogger(BuilderScreenService.class.getName());

    public Page<BuilderScreen> findAllForUser(Pageable pageable, Integer userId) {
        logger.info("Buscando todas as telas visíveis para o usuário: " + userId);
        return repository.findVisibleScreensForUser(pageable, userId);
    }

    public Page<BuilderScreen> findAllComponentForUser(Pageable pageable, Integer userId) {
        logger.info("Buscando todos os componentes sem grupo visíveis para o usuário: " + userId);
        return repository.findVisibleComponentsWithoutGroupForUser(pageable, userId);
    }

    public Page<BuilderScreen> findComponentByGroupForUser(Pageable pageable, Long groupId, Integer userId) {
        logger.info("Buscando componentes do grupo " + groupId + " visíveis para o usuário: " + userId);
        return repository.findVisibleComponentsByGroupForUser(pageable, groupId, userId);
    }

    public BuilderScreen findByIdForUser(Long id, Integer userId) {
        logger.info("Buscando tela por ID: " + id);
        if (!permissionService.canView(userId, id, "BUILDER_SCREEN")) {
            throw new AccessDeniedException("Acesso negado para visualizar esta tela.");
        }
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nenhum registro para este ID!"));
    }

    public List<BuilderScreen> buildingScreenWithPreviewForUser(Integer userId) {
        logger.info("Buscando telas com preview para o usuário: " + userId);
        return repository.findVisibleScreensWithPreviewForUser(userId);
    }

    public BuilderScreen buildingScreenCompleteForUser(Long id, Integer userId) {
        logger.info("Montando tela completa por ID: " + id);
        BuilderScreen screen = findByIdForUser(id, userId); // Reutiliza o método seguro de busca por ID

        Gson gson = new Gson();
        List<BuilderDiv> allDivs = builderDivRepository.findDivToBuilding(screen.getId());
        allDivs.forEach(div -> {
            List<BuilderField> fields = builderFieldRepository.buildingScreenFieldsExcludePreview(div.getId());
            div.setFieldJson(gson.toJson(fields));
        });
        screen.setDivJson(gson.toJson(allDivs));
        return screen;
    }

    public BuilderScreen create(BuilderScreen item, Integer userId) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();
        // Para criar, a permissão pode ser genérica, não atrelada a um recurso específico (ID 0L).
        if (!permissionService.canCreate(userId, 0L, "BUILDER_SCREEN")) {
            throw new AccessDeniedException("Acesso negado: você não tem permissão para criar telas.");
        }
        logger.info("Criando uma nova tela...");
        return repository.save(item);
    }

    public BuilderScreen update(BuilderScreen item, Integer userId) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();
        if (!permissionService.canEdit(userId, item.getId(), "BUILDER_SCREEN")) {
            throw new AccessDeniedException("Acesso negado: você não tem permissão para editar esta tela.");
        }
        logger.info("Atualizando a tela ID: " + item.getId());
        BuilderScreen old = findByIdForUser(item.getId(), userId); // Garante que o usuário pode ver o que está editando
        old.setTitle(item.getTitle());
        old.setIcon(item.getIcon());
        old.setTableName(item.getTableName());
        return repository.save(old);
    }

    public void delete(Long id, Integer userId) {
        if (!permissionService.canDelete(userId, id, "BUILDER_SCREEN")) {
            throw new AccessDeniedException("Acesso negado: você não tem permissão para excluir esta tela.");
        }
        logger.info("Excluindo a tela ID: " + id);
        BuilderScreen old = findByIdForUser(id, userId);
        repository.delete(old);
    }
}