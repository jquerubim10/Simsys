package br.com.savemed.controllers;

import br.com.savemed.model.auth.UserSave;
import br.com.savemed.model.builder.BuilderScreen;
import br.com.savemed.services.BuilderScreenService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/builder-screen")
@Tag(name = "Builder Screen", description = "Endpoints para Gerenciar Telas Dinâmicas")
public class BuilderScreenController {

    @Autowired
    private BuilderScreenService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Builder Screen", description = "Finds all Builder Screen", tags = {"Builder Screen"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = BuilderScreen.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<BuilderScreen>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                       @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                       @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                       @AuthenticationPrincipal UserSave user) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        // O método deve apenas chamar o serviço com o pageable que o Spring cria.
        return ResponseEntity.ok(service.findAllForUser(pageable, user.getUsuario()));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Finds a Builder Screen", description = "Finds a Builder Screen", tags = {"Builder Screen"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BuilderScreen.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public BuilderScreen findById(@PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        return service.findByIdForUser(id, user.getUsuario());
    }

    @GetMapping(value = "/previews")
    @Operation(summary = "Get all Screen With Preview", description = "Get all Screen With Preview", tags = {"Builder Screen"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = BuilderScreen.class))
                                    )
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public List<BuilderScreen> buildingScreenWithPreview(@AuthenticationPrincipal UserSave user) {
        return service.buildingScreenWithPreviewForUser(user.getUsuario());
    }

    @GetMapping(value = "/{id}/complete")
    @Operation(summary = "Building Screen Complete", description = "Building Screen Complete", tags = {"Builder Screen"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BuilderScreen.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public BuilderScreen buildingScreenComplete(@PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        return service.buildingScreenCompleteForUser(id, user.getUsuario());
    }

    @GetMapping(value = "/component")
    @Operation(summary = "Finds all Component Screen", description = "Finds all Component Screen", tags = {"Builder Screen"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = BuilderScreen.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<BuilderScreen>> findAllComponent(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                @AuthenticationPrincipal UserSave user) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findAllComponentForUser(pageable, user.getUsuario()));
    }

    @GetMapping(value = "/component/group/{id}")
    @Operation(summary = "Finds all Component Screen", description = "Finds all Component Screen", tags = {"Builder Screen"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = BuilderScreen.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<BuilderScreen>> findComponentByGroup(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                @RequestParam(value = "size", defaultValue = "99") Integer size,
                                                                @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                @PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findComponentByGroupForUser(pageable, id, user.getUsuario()));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Adds a new Builder Screen", description = "Adds a new Builder Screen by passing in a JSON, XML or YML representation of the panel!", tags = {"Builder Screen"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BuilderScreen.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public BuilderScreen create(@RequestBody BuilderScreen item, @AuthenticationPrincipal UserSave user) {
        return service.create(item, user.getUsuario());
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Updates a Builder Screen", description = "Updates a Builder Screen by passing in a JSON, XML or YML representation of the panel!", tags = {"Builder Screen"},
            responses = {
                    @ApiResponse(description = "Updated", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BuilderScreen.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public BuilderScreen update(@RequestBody BuilderScreen item, @AuthenticationPrincipal UserSave user) {
        return service.update(item, user.getUsuario());
    }

    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Disable a specific Builder Screen by your ID", description = "Disable a specific Builder Screen by your ID", tags = {"Builder Screen"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BuilderScreen.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public BuilderScreen disablePanel(@PathVariable(value = "id") Long id) {
        return service.disableBuilderScreen(id);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletes a Builder Screen", description = "Deletes a Builder Screen by Id!", tags = {"Builder Screen"},
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
