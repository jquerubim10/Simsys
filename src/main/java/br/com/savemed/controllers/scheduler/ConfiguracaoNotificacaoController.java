package br.com.savemed.controllers.scheduler;

import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.scheduler.ConfiguracaoNotificacao;
import br.com.savemed.services.scheduler.ConfiguracaoNotificacaoService;
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
@RequestMapping("/api/v1/scheduler/configuracao-notificacao")
@Tag(name = "configuracao-notificacao", description = "Endpoints for Managing ConfiguracaoNotificacao")
public class ConfiguracaoNotificacaoController {

    private final ConfiguracaoNotificacaoService service;

    @Autowired
    public ConfiguracaoNotificacaoController(ConfiguracaoNotificacaoService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Configuracoes", tags = {"configuracao-notificacao"})
    public ResponseEntity<Page<ConfiguracaoNotificacao>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                                 @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search Configuracoes by SQL", tags = {"configuracao-notificacao"})
    public ResponseEntity<Page<ConfiguracaoNotificacao>> findAllBySql(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                      @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                                      @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                      @RequestBody QueryBody queryBody) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(service.findAllBySql(pageable, queryBody));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find one Configuracao by ID", tags = {"configuracao-notificacao"})
    public ConfiguracaoNotificacao findById(@PathVariable(value = "id") Long id) throws Exception {
        return service.findById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Creates a ConfiguracaoNotificacao", tags = {"configuracao-notificacao"})
    public ConfiguracaoNotificacao create(@RequestBody ConfiguracaoNotificacao item) {
        return service.create(item);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletes a ConfiguracaoNotificacao", tags = {"configuracao-notificacao"})
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
