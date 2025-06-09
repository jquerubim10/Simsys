package br.com.savemed.controllers;

import br.com.savemed.config.HibernateUtil;
import br.com.savemed.controllers.interfaces.ListResultTransformer;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.query.QueryReturn;
import br.com.savemed.util.SqlConstraints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings({"unchecked", "unsafe"})
@RestController
@RequestMapping("/api/v1/preview")
@Tag(name = "preview", description = "Endpoints for Managing preview")
public class PreviewController {

    @Autowired
    @PersistenceContext
    EntityManager entityManager;

    @GetMapping(value = "header", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find header preview", description = "Find header preview", tags = {"preview"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<?> getHeader() {
        jakarta.persistence.Query query1 = entityManager.createNativeQuery("select * from V_RPT_HEADER", QueryReturn.class);

        List<?> result = query1.unwrap(org.hibernate.query.Query.class)
                .setTupleTransformer(
                        (ListResultTransformer)
                                (tuple, aliases) -> new QueryReturn(aliases, tuple)
                ).getResultList();

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "evolution/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find evolution preview", description = "Find evolution preview", tags = {"preview"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<?> getEvolution(@PathVariable("id") Long id) {

        String query = SqlConstraints.PREVIEW_EVOLUTION + id;
        jakarta.persistence.Query getLastId = entityManager.createNativeQuery(query, QueryReturn.class);

        List<?> result = getLastId.unwrap(org.hibernate.query.Query.class)
                .setTupleTransformer(
                        (ListResultTransformer)
                                (tuple, aliases) -> new QueryReturn(aliases, tuple)
                ).getResultList();

        return ResponseEntity.ok(result);
    }

    @PostMapping(name = "generic/", value = "/generic/{id}")
    @Operation(summary = "Find generic preview", description = "Find generic preview", tags = {"preview"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<List<?>> getGenericPreview(@RequestBody QueryBody item, @PathVariable("id") Long id) {
        String query = item.getSelectOne();
        jakarta.persistence.Query getLastId = entityManager.createNativeQuery(query, QueryReturn.class);

        List<?> result = getLastId.unwrap(org.hibernate.query.Query.class)
                .setTupleTransformer(
                        (ListResultTransformer)
                                (tuple, aliases) -> new QueryReturn(aliases, tuple)
                ).getResultList();

        return ResponseEntity.ok(result);
    }




}
