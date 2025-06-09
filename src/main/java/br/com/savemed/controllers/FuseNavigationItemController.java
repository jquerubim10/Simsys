package br.com.savemed.controllers;

import br.com.savemed.model.FuseNavigationItem;
import br.com.savemed.services.FuseNavigationItemService;
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
@RequestMapping("/api/v1/fuse/sidebar/")
@Tag(name = "Sidebar", description = "Endpoints for Managing Sidebar fuse")
public class FuseNavigationItemController {

    @Autowired
    private FuseNavigationItemService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Sidebar", description = "Finds all Sidebar", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = FuseNavigationItem.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<FuseNavigationItem>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "meta"));

        return ResponseEntity.ok(service.findAll(pageable));
    }


    @GetMapping(value = "/children/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Sidebar children", description = "Finds all Sidebar children", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = FuseNavigationItem.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<FuseNavigationItem>> findAllSidebarChildren(@PathVariable(value = "id") Long id,
                                                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                        @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                        @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findAllSidebarChildren(pageable, id));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds a Sidebar", description = "Finds a Sidebar", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = FuseNavigationItem.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public FuseNavigationItem findById(@PathVariable(value = "id") Long id) throws Exception {
        return service.findById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Adds a new Sidebar", description = "Adds a new Sidebar by passing in a JSON, XML or YML representation of the person!", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = FuseNavigationItem.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public FuseNavigationItem create(@RequestBody FuseNavigationItem item) {
        return service.create(item);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Updates a Sidebar", description = "Updates a Sidebar by passing in a JSON, XML or YML representation of the person!", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Updated", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = FuseNavigationItem.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public FuseNavigationItem update(@RequestBody FuseNavigationItem item) {
        return service.update(item);
    }

    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Disable a specific Sidebar by your ID", description = "Disable a specific Sidebar by your ID", tags = {"Sidebar"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = FuseNavigationItem.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public FuseNavigationItem disableSidebar(@PathVariable(value = "id") Long id) {
        return service.disableMenu(id);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletes a Sidebar", description = "Deletes a Sidebar by Id!", tags = {"Sidebar"},
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
