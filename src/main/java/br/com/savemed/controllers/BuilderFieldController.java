package br.com.savemed.controllers;

import br.com.savemed.model.builder.BuilderField;
import br.com.savemed.services.BuilderFieldService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fuse/builder/field")
@Tag(name = "Builder Field", description = "Endpoints for Managing Builder Field")
public class BuilderFieldController {

    @Autowired
    private BuilderFieldService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Builder Field", description = "Finds all Builder Field", tags = {"Builder Field"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = BuilderField.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<BuilderField>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                      @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findAll(pageable));
    }


    @GetMapping(value = "list/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Builder Field by Screen", description = "Finds all Builder Field", tags = {"Builder Field"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = BuilderField.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<BuilderField>> findAllFieldByScreen(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                   @RequestParam(value = "size", defaultValue = "20") Integer size,
                                                                   @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                   @PathVariable(value = "id") Long id) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "divId"));

        return ResponseEntity.ok(service.findAllFilterScreen(pageable, id));
    }

    @GetMapping(value = "search/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Builder Field search", description = "Finds all Builder Field search", tags = {"Builder Field"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = BuilderField.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<BuilderField>> findSearchField(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                              @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                              @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                              @PathVariable(value = "id") Long id) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findFieldSearch(pageable, id));

    }

    @GetMapping(value = "div/list/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Builder Field by Screen", description = "Finds all Builder Field", tags = {"Builder Field"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = BuilderField.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<BuilderField>> findAllFieldByDiv(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                   @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                                   @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                   @PathVariable(value = "id") Long id) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "orderRow"));

        return ResponseEntity.ok(service.findAllFilterDiv(pageable, id));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds a Builder Field", description = "Finds a Builder Field", tags = {"Builder Field"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BuilderField.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public BuilderField findById(@PathVariable(value = "id") Long id) throws Exception {
        return service.findById(id);
    }

    @GetMapping(name = "findIdentityList", value = "/{id}/identity", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds a Builder Field identity", description = "Finds a Builder Field identity", tags = {"Builder Field"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = BuilderField.class))
                            )
                    }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<BuilderField>> findIdentityList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                               @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                               @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                               @PathVariable(value = "id") Long id) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findIdentityList(pageable, id));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Adds a new Builder Field", description = "Adds a new Builder Field by passing in a JSON, XML or YML representation of the panel!", tags = {"Builder Field"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BuilderField.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public BuilderField create(@RequestBody BuilderField item) {
        return service.create(item);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Updates a Builder Field", description = "Updates a Builder Field by passing in a JSON, XML or YML representation of the panel!", tags = {"Builder Field"},
            responses = {
                    @ApiResponse(description = "Updated", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BuilderField.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public BuilderField update(@RequestBody BuilderField item) {
        return service.update(item);
    }

    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Disable a specific Builder Field by your ID", description = "Disable a specific Builder Field by your ID", tags = {"Builder Field"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BuilderField.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public BuilderField disablePanel(@PathVariable(value = "id") Long id) {
        return service.disableDiv(id);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletes a Builder Field", description = "Deletes a Builder Field by Id!", tags = {"Builder Field"},
            responses = {
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
