package br.com.savemed.controllers;

import br.com.savemed.config.HibernateUtil;
import br.com.savemed.controllers.interfaces.ListResultTransformer;
import br.com.savemed.model.CentroCusto;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.query.QueryReturn;
import br.com.savemed.model.savemed.CentroCustoSavemed;
import br.com.savemed.services.savemed.CentroCustoSavemedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@SuppressWarnings({"unchecked", "unsafe"})
@RequestMapping("/api/v1/center/cost")
@Tag(name = "Centro custo", description = "Endpoints for Managing Centro custo")
public class CentroCustoController {

    private final Logger logger = Logger.getLogger(CentroCustoController.class.getName());

    @Autowired
    CentroCustoSavemedService service;

    @Autowired
    @PersistenceContext
    EntityManager entityManager;

    public String sql  = " SELECT A.CENTROCUSTO, A.DESCRICAO \n" +
    "FROM CENTROCUSTO A WITH (NOLOCK) WHERE 1 = 1 \n" +
    "AND A.CENTROCUSTO IN ( \n" +
    "        SELECT CENTROCUSTO \n" +
	"					FROM TUTORCENTROCUSTO WITH (NOLOCK) \n" +
    "WHERE  TUTOR = ";
     public String sql2 = ") and A.CENTROCUSTO IN ";
     public String sql3 = "ORDER BY A.DESCRICAO";

    @PostMapping()
    public ResponseEntity<?> all(@RequestBody QueryBody item) {
        logger.info("[init] -------- [CC] execute select to get response list");

        List<QueryReturn> result = new ArrayList<>();
        try {
            String query = sql + item.getTutorUser() + sql2 + item.getArrayCenter() + sql3;
            jakarta.persistence.Query query1 = entityManager.createNativeQuery(query, QueryReturn.class);

            result = query1.unwrap(org.hibernate.query.Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)
                    ).getResultList();

            logger.info("[finish] -------- [200] [List not empty] [CC] execute select to get response list");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.info("[finish] -------- [400] [CC] execute select to get response list");
            return ResponseEntity.badRequest().body(e.fillInStackTrace());
        }
    }

    @GetMapping(value = "/setor", produces = MediaType.APPLICATION_JSON_VALUE)
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
                                                            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(direction) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(service.findAllSetor(pageable));
    }
}
