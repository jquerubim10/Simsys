package br.com.savemed.controllers;

import br.com.savemed.model.NavigationItem;
import br.com.savemed.model.auth.UserSave;
import br.com.savemed.services.NavigationItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/navigation") // Endpoint genérico
@Tag(name = "Navigation", description = "Endpoints para Gerenciar Itens de Navegação")
public class NavigationItemController {

    @Autowired
    private NavigationItemService service;

    /**
     * Mantido, mas agora seguro: Busca os menus raiz que o USUÁRIO LOGADO pode ver.
     */
    @GetMapping(produces = "application/json")
    @Operation(summary = "Busca os itens de menu raiz do usuário logado")
    public ResponseEntity<Page<NavigationItem>> findAll(Pageable pageable, @AuthenticationPrincipal UserSave user) {
        // INJEÇÃO DO USUÁRIO: A anotação @AuthenticationPrincipal pega o usuário validado pelo SecurityFilter.
        // LÓGICA DE NEGÓCIO SEGURA: Chamamos o método do serviço que filtra os resultados por permissão.
        return ResponseEntity.ok(service.findAllForUser(pageable, user.getUsuario()));
    }

    /**
     * Mantido, mas agora seguro: Busca os menus filhos que o USUÁRIO LOGADO pode ver.
     */
    @GetMapping(value = "/children/{id}", produces = "application/json")
    @Operation(summary = "Busca os itens de menu filhos de um item específico")
    public ResponseEntity<Page<NavigationItem>> findAllSidebarChildren(@PathVariable(value = "id") Long id,
                                                                       Pageable pageable,
                                                                       @AuthenticationPrincipal UserSave user) {
        return ResponseEntity.ok(service.findAllChildrenForUser(pageable, id, user.getUsuario()));
    }

    /**
     * Mantido, mas agora seguro: Busca um item por ID apenas se o usuário tiver permissão para vê-lo.
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Busca um item de menu por ID")
    public NavigationItem findById(@PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        return service.findByIdForUser(id, user.getUsuario());
    }

    /**
     * Mantido, mas agora seguro: Cria um item de menu. A permissão é verificada no serviço.
     */
    @PostMapping(produces = "application/json", consumes = "application/json")
    @Operation(summary = "Adiciona um novo item de menu")
    public NavigationItem create(@RequestBody NavigationItem item, @AuthenticationPrincipal UserSave user) {
        return service.create(item, user.getUsuario());
    }

    /**
     * Mantido, mas agora seguro: Atualiza um item de menu. A permissão é verificada no serviço.
     */
    @PutMapping(produces = "application/json", consumes = "application/json")
    @Operation(summary = "Atualiza um item de menu")
    public NavigationItem update(@RequestBody NavigationItem item, @AuthenticationPrincipal UserSave user) {
        return service.update(item, user.getUsuario());
    }

    /**
     * Mantido, mas agora seguro: Desabilita um item de menu. A permissão (de edição) é verificada no serviço.
     */
    @PatchMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Desabilita um item de menu específico")
    public NavigationItem disableSidebar(@PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        return service.disableMenu(id, user.getUsuario());
    }

    /**
     * Mantido, mas agora seguro: Deleta um item de menu. A permissão é verificada no serviço.
     */
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deleta um item de menu")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        service.delete(id, user.getUsuario());
        return ResponseEntity.noContent().build();
    }
}