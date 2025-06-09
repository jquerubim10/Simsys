package br.com.savemed.controllers.scheduler;

import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.scheduler.Notificacao;
import br.com.savemed.services.scheduler.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scheduler/notificacao")
@Tag(name = "notificacao", description = "Endpoints for Managing Notificacao")
public class NotificacaoController {

    private final NotificacaoService service;

    @Autowired
    public NotificacaoController(NotificacaoService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Notificacoes", description = "Finds all Notificacoes", tags = {"notificacao"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notificacao.class)))),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<Notificacao>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                     @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search Notificacoes by SQL", description = "Search Notificacoes with custom SQL", tags = {"notificacao"})
    public ResponseEntity<Page<Notificacao>> findAllBySql(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                          @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                          @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                          @RequestBody QueryBody queryBody) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(service.findAllBySql(pageable, queryBody));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds one Notificacao by ID", tags = {"notificacao"})
    public Notificacao findById(@PathVariable(value = "id") Long id) throws Exception {
        return service.findById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Adds a new Notificacao", tags = {"notificacao"})
    public Notificacao create(@RequestBody Notificacao item) {
        return service.create(item);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletes a Notificacao by ID", tags = {"notificacao"})
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
