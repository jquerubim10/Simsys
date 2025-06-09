package br.com.savemed.controllers.scheduler;

import br.com.savemed.model.CentroCusto;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.savemed.CentroCustoSavemed;
import br.com.savemed.model.scheduler.Agendamento;
import br.com.savemed.model.scheduler.Recurso;
import br.com.savemed.services.scheduler.RecursoService;
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
@RequestMapping("/api/v1/scheduler/recurso")
@Tag(name = "recurso", description = "Endpoints for Managing Recurso")
public class RecursoController {

    private final RecursoService service;

    @Autowired
    public RecursoController(RecursoService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Recursos", description = "Finds all Recursos", tags = {"recurso"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = Recurso.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<Recurso>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                     @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PostMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Recursos by sql", description = "Finds all Recursos by sql", tags = {"recurso"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = Recurso.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<Recurso>> findAllByText(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                 @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                 @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                 @RequestBody QueryBody queryBody) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findAllBySql(pageable, queryBody));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds one Recurso", description = "Finds one Recurso", tags = {"recurso"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Recurso.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public Recurso findById(@PathVariable(value = "id") Long id) throws Exception {
        return service.findById(id);
    }

    @GetMapping(value = "/{id}/setor", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Setor", description = "Finds all Setor", tags = {"recurso"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = CentroCustoSavemed.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<CentroCustoSavemed>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                            @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                            @PathVariable(value = "id") Long id) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findAllSetor(pageable, id));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Adds a new Recurso", description = "Adds a new Recurso!", tags = {"recurso"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Recurso.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public Recurso create(@RequestBody Recurso item) {
        return service.create(item);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a Recurso", description = "Delete a Recurso!", tags = {"recurso"},
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
