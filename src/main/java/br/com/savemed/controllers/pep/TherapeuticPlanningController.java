package br.com.savemed.controllers.pep;

import br.com.savemed.config.HibernateUtil;
import br.com.savemed.controllers.interfaces.ListResultTransformer;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.query.QueryReturn;
import br.com.savemed.util.SqlConstraints;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@SuppressWarnings({"unchecked", "unsafe"})
@RequestMapping("/api/v1/therapeutic/planning")
@Tag(name = "Charts", description = "Endpoints for Managing Charts")
public class TherapeuticPlanningController {
    private final Logger logger = Logger.getLogger(TherapeuticPlanningController.class.getName());

    @Autowired
    @PersistenceContext
    EntityManager entityManager;

    @PostMapping(value = "/list",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> get(@RequestBody QueryBody item) {
        logger.info("[init] -------- [therapeutic] execute select to get response list");

        List<QueryReturn> result = List.of();
        try {
            String query = SqlConstraints.SQL_LIST_THERAPEUTIC_PLANNING +
                    item.getTpTreatment() + " A INNER JOIN MOVIM_PRES_" + item.getTpTreatment() +
                    SqlConstraints.SQL_LIST_THERAPEUTIC_PLANNING_INNER +
                    item.getWhereValue() +
                    SqlConstraints.SQL_LIST_THERAPEUTIC_PLANNING_GROUP +
                    SqlConstraints.SQL_LIST_THERAPEUTIC_PLANNING_UNION_ALL_1 +
                    item.getSecondWhereValue() +  " AND C.NECESSARIO = 1";

            System.out.println(query);
            jakarta.persistence.Query query1 = entityManager.createNativeQuery(query, QueryReturn.class);

            result = query1.unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)
                    ).getResultList();

            HibernateUtil.shutdown();
            logger.info("[finish] -------- [200] [therapeutic] execute select to get response list");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            if (result.isEmpty()) {
                HibernateUtil.shutdown();
                logger.info("[finish] -------- [500] [therapeutic] execute select to get response list");
                return ResponseEntity.internalServerError().body("Result resolve in a empty List");
            } else {
                HibernateUtil.shutdown();
                logger.info("[finish] -------- [500] [therapeutic] execute select to get response list");
                return ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());
            }
        }
    }
}
