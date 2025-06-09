package br.com.savemed.controllers.savemed;

import br.com.savemed.model.savemed.CentroCustoSavemed;
import br.com.savemed.services.savemed.CentroCustoSavemedService;
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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/savemed/centrocusto")
@Tag(name = "savemed", description = "Endpoints for Managing savemed Centro custo")
public class CentroCustoSavemedController {

    private final Logger logger = Logger.getLogger(CentroCustoSavemedController.class.getName());

    @Autowired
    CentroCustoSavemedService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all centrocusto", description = "Finds all centrocusto", tags = {"savemed"},
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
                                               @RequestParam(value = "size", defaultValue = "12") Integer size,
                                               @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "centrocusto"));

        return ResponseEntity.ok(service.findAll(pageable));
    }
}
