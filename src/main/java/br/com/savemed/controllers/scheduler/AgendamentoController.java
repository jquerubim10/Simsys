package br.com.savemed.controllers.scheduler;

import br.com.savemed.model.Panel;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.savemed.MedicoSavemed;
import br.com.savemed.model.scheduler.Agendamento;
import br.com.savemed.model.scheduler.AgendamentoRecurso;
import br.com.savemed.model.scheduler.Convenio;
import br.com.savemed.model.scheduler.EquipeAgendamento;
import br.com.savemed.services.scheduler.AgendamentoRecursoService;
import br.com.savemed.services.scheduler.AgendamentoService;
import br.com.savemed.services.scheduler.EquipeAgendamentoService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/scheduler")
@Tag(name = "scheduler", description = "Endpoints for Managing Agendamentos")
public class AgendamentoController {


    private final AgendamentoService service;

    private final AgendamentoRecursoService agendamentoRecursoService;

    private final EquipeAgendamentoService equipeAgendamentoService;

    private final Logger logger = Logger.getLogger(AgendamentoController.class.getName());

    @Autowired
    public AgendamentoController(AgendamentoService service, AgendamentoRecursoService agendamentoRecursoService, EquipeAgendamentoService equipeAgendamentoService) {
        this.service = service;
        this.agendamentoRecursoService = agendamentoRecursoService;
        this.equipeAgendamentoService = equipeAgendamentoService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Agendamentos", description = "Finds all Agendamentos", tags = {"scheduler"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = Agendamento.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<Agendamento>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                    @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                    @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping(value = "/{id}/resources", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Agendamentos Recursos", description = "Finds all Agendamentos Recursos", tags = {"scheduler"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = AgendamentoRecurso.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<AgendamentoRecurso>> findAllRecursoAgendamento(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                        @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                        @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                        @PathVariable(value = "id") Long id) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(agendamentoRecursoService.findAllByAgendamento(pageable, id));
    }

    @GetMapping(value = "/{id}/teams", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Equipe Agendamento", description = "Finds all Equipe Agendamento", tags = {"scheduler"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = EquipeAgendamento.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<EquipeAgendamento>> findAllEquipeAgendamento(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                            @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                                            @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                            @PathVariable(value = "id") Long id) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(equipeAgendamentoService.findAllTeamsByScheduler(pageable, id));
    }

    @PostMapping(value = "/filtered", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Scheduler filtered", description = "Finds all Scheduler filtered", tags = {"scheduler"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = Agendamento.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<Agendamento>> findAllFiltered(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                             @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                             @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                             @RequestBody QueryBody body) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findAllSql(pageable, body));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds one Agendamento", description = "Finds one Agendamento", tags = {"scheduler"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Agendamento.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public Agendamento findById(@PathVariable(value = "id") Long id) throws Exception {
        return service.findById(id);
    }

    @GetMapping(value = "/convenio", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Convenio", description = "Finds all Convenio", tags = {"scheduler"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = Convenio.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<Convenio>> findAllConvenios(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                              @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                              @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "descricao"));

        return ResponseEntity.ok(service.findAllConvenios(pageable));
    }

    @GetMapping(value = "logged/{id_user}/center/{id_center}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all Doctors", description = "Finds all Doctors", tags = {"scheduler"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = MedicoSavemed.class))
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Page<MedicoSavemed>> findAllDoctors(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "999") Integer size,
                                                     @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                              @PathVariable(value = "id_user") Long idUser, @PathVariable(value = "id_center") Long idCenter) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "nome"));

        return ResponseEntity.ok(service.findAllDoctors(pageable, idUser, idCenter));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Adds a new Agendamento", description = "Adds a new Agendamento!", tags = {"scheduler"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201",
                            content = @Content(schema = @Schema(implementation = Agendamento.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Object> create(@RequestBody Agendamento item) throws JsonProcessingException {
        Agendamento scheduler = service.create(item);

        if (Objects.nonNull(scheduler)) {
            logger.info("Agendamento realizado com successo!!");
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/update/status",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Updates a status Agendamento", description = "Updates a status Agendamento", tags = {"scheduler"},
            responses = {
                    @ApiResponse(description = "Updated", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Agendamento.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<?> updateStatus(@RequestBody QueryBody item) {
        Agendamento a = service.updateStatus(item);
        return ResponseEntity.ok().build();
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Updates a Agendamento", description = "Updates a Agendamento!", tags = {"scheduler"},
            responses = {
                    @ApiResponse(description = "Updated", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Panel.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public Agendamento update(@RequestBody Agendamento item) throws JsonProcessingException {
        return service.update(item);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a Agendamento", description = "Delete a Agendamento!", tags = {"scheduler"},
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
