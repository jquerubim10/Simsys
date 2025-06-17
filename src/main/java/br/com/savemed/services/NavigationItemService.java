package br.com.savemed.services;

import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.NavigationItem;
import br.com.savemed.repositories.NavigationItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Serviço para gerenciar a lógica de negócio dos Itens de Navegação.
 * Esta classe é responsável por orquestrar as operações e garantir
 * que as verificações de permissão sejam aplicadas antes de qualquer ação.
 */
@Service
public class NavigationItemService {

    private final Logger logger = Logger.getLogger(NavigationItemService.class.getName());

    @Autowired
    private NavigationItemRepository repository;

    @Autowired
    private PermissionService permissionService; // Serviço central de autorização

    // --- MÉTODOS DE CONSULTA SEGUROS ---

    /**
     * Busca os menus raiz que o usuário especificado tem permissão para visualizar.
     * A segurança é garantida pela query no repositório.
     */
    public Page<NavigationItem> findAllForUser(Pageable pageable, Integer userId) {
        logger.info("Buscando menus raiz visíveis para o usuário ID: " + userId);
        if (userId == null) return Page.empty();
        return repository.findVisibleMenusForUser(pageable, userId);
    }

    /**
     * Busca os menus filhos de um pai específico que o usuário tem permissão para visualizar.
     * A segurança é garantida pela query no repositório.
     */
    public Page<NavigationItem> findAllChildrenForUser(Pageable pageable, Long parentId, Integer userId) {
        logger.info("Buscando menus filhos visíveis para o pai " + parentId + " e usuário ID: " + userId);
        if (userId == null) return Page.empty();
        return repository.findVisibleChildrenForUser(pageable, parentId, userId);
    }

    /**
     * Busca um item de menu específico pelo ID, mas somente se o usuário tiver permissão para vê-lo.
     */
    public NavigationItem findByIdForUser(Long id, Integer userId) {
        logger.info("Buscando item de menu ID: " + id + " para o usuário ID: " + userId);

        // Verifica permissão primeiro
        if (!permissionService.canView(userId, id, "NAVIGATION_ITEM")) {
            throw new AccessDeniedException("Acesso negado: você não tem permissão para visualizar este item.");
        }

        try {
            NavigationItem item = repository.findByIdWithRelations(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
            // Tenta buscar o item
            return item;
        } catch (Exception e) {
            logger.severe("Erro ao buscar item de menu ID: " + id + " - " + e.getMessage());
            throw new ResourceNotFoundException("Erro ao buscar item de menu"+ e.getMessage());
        }
    }

    // --- MÉTODOS DE ESCRITA SEGUROS ---

    /**
     * Cria um novo item de menu após verificar a permissão do usuário.
     */
    public NavigationItem create(NavigationItem item, Integer userId) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        // A permissão para criar um item filho é verificada no seu item pai.
        // Para itens raiz (sem pai), usamos um ID de recurso genérico como 0L.
        Long resourceIdParaVerificar = (item.getParent() != null) ? item.getParent().getId() : 0L;
        if (!permissionService.canCreate(userId, resourceIdParaVerificar, "NAVIGATION_ITEM")) {
            throw new AccessDeniedException("Acesso negado: permissão para criar item de menu recusada.");
        }

        logger.info("Criando um novo item de menu...");
        return repository.save(item);
    }

    /**
     * Atualiza um item de menu existente após verificar a permissão do usuário.
     */
    public NavigationItem update(NavigationItem item, Integer userId) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();

        // Antes de qualquer ação, verifica se o usuário PODE EDITAR este recurso específico.
        if (!permissionService.canEdit(userId, item.getId(), "NAVIGATION_ITEM")) {
            throw new AccessDeniedException("Acesso negado: permissão para editar este item de menu recusada.");
        }

        logger.info("Atualizando o item de menu ID: " + item.getId());
        NavigationItem entity = repository.findById(item.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID!"));

        // Lógica de atualização
        entity.setTitle(item.getTitle());
        entity.setIdName(item.getIdName());
        entity.setLink(item.getLink());
        entity.setType(item.getType());
        entity.setIcon(item.getIcon());
        entity.setHidden(item.isHidden());
        entity.setDisabled(item.isDisabled());

        return repository.save(entity);
    }

    /**
     * Desabilita um item de menu. Considerado uma forma de "edição", portanto, verifica canEdit.
     */
    @Transactional
    public NavigationItem disableMenu(Long id, Integer userId) {
        // Verifica se o usuário tem permissão para editar/desabilitar.
        if (!permissionService.canEdit(userId, id, "NAVIGATION_ITEM")) {
            throw new AccessDeniedException("Acesso negado: permissão para desabilitar este item de menu recusada.");
        }

        logger.info("Desabilitando o item de menu ID: " + id);
        NavigationItem entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID!"));

        entity.setDisabled(true); // Lógica de negócio movida para o serviço

        return repository.save(entity);
    }

    /**
     * Deleta um item de menu após verificar a permissão do usuário.
     */
    public void delete(Long id, Integer userId) {
        // Antes de deletar, verifica se o usuário PODE EXCLUIR.
        if (!permissionService.canDelete(userId, id, "NAVIGATION_ITEM")) {
            throw new AccessDeniedException("Acesso negado: permissão para excluir este item de menu recusada.");
        }

        logger.info("Deletando o item de menu ID: " + id);
        NavigationItem entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID!"));
        repository.delete(entity);
    }
}