package br.com.savemed.controllers;

import br.com.savemed.model.NavigationItem;
import br.com.savemed.model.auth.UserSave;
import br.com.savemed.services.NavigationItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @Operation(summary = "Finds all Sidebar", description = "Finds all Sidebar", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = NavigationItem.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<NavigationItem>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                        @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                        @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                        @AuthenticationPrincipal UserSave user) {
        // INJEÇÃO DO USUÁRIO: A anotação @AuthenticationPrincipal pega o usuário validado pelo SecurityFilter.

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "meta"));

        // LÓGICA DE NEGÓCIO SEGURA: Chamamos o método do serviço que filtra os resultados por permissão.
        return ResponseEntity.ok(service.findAllForUser(pageable, user.getUsuario()));
    }

    /**
     * Mantido, mas agora seguro: Busca os menus filhos que o USUÁRIO LOGADO pode ver.
     */
    @GetMapping(value = "/children/{id}", produces = "application/json")
    @Operation(summary = "Finds all Sidebar children", description = "Finds all Sidebar children", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = NavigationItem.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<NavigationItem>> findAllSidebarChildren(@PathVariable(value = "id") Long id,
                                                                       Pageable pageable,
                                                                       @AuthenticationPrincipal UserSave user) {
        return ResponseEntity.ok(service.findAllChildrenForUser(pageable, id, user.getUsuario()));
    }

    /**
     * Mantido, mas agora seguro: Busca um item por ID apenas se o usuário tiver permissão para vê-lo.
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Finds a Sidebar", description = "Finds a Sidebar", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = NavigationItem.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public NavigationItem findById(@PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        return service.findByIdForUser(id, user.getUsuario());
    }

    /**
     * Mantido, mas agora seguro: Cria um item de menu. A permissão é verificada no serviço.
     */
    @PostMapping(produces = "application/json", consumes = "application/json")
    @Operation(summary = "Adds a new Sidebar", description = "Adds a new Sidebar by passing in a JSON, XML or YML representation of the person!", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = NavigationItem.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public NavigationItem create(@RequestBody NavigationItem item, @AuthenticationPrincipal UserSave user) {
        return service.create(item, user.getUsuario());
    }

    /**
     * Mantido, mas agora seguro: Atualiza um item de menu. A permissão é verificada no serviço.
     */
    @PutMapping(produces = "application/json", consumes = "application/json")
    @Operation(summary = "Updates a Sidebar", description = "Updates a Sidebar by passing in a JSON, XML or YML representation of the person!", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Updated", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = NavigationItem.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public NavigationItem update(@RequestBody NavigationItem item, @AuthenticationPrincipal UserSave user) {
        return service.update(item, user.getUsuario());
    }

    /**
     * Mantido, mas agora seguro: Desabilita um item de menu. A permissão (de edição) é verificada no serviço.
     */
    @PatchMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Disable a specific Sidebar by your ID", description = "Disable a specific Sidebar by your ID", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = NavigationItem.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public NavigationItem disableSidebar(@PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        return service.disableMenu(id, user.getUsuario());
    }

    /**
     * Mantido, mas agora seguro: Deleta um item de menu. A permissão é verificada no serviço.
     */
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletes a Sidebar", description = "Deletes a Sidebar by Id!", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        service.delete(id, user.getUsuario());
        return ResponseEntity.noContent().build();
    }
}