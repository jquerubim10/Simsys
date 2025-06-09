package br.com.savemed.controllers;

import br.com.savemed.config.HibernateUtil;
import br.com.savemed.controllers.interfaces.ListResultTransformer;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.query.QueryReturn;
import br.com.savemed.util.SqlConstraints;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@SuppressWarnings({"unchecked", "unsafe"})
@RequestMapping("/api/v1/treatment")
public class TreatmentDynamic {
    private final Logger logger = Logger.getLogger(TreatmentDynamic.class.getName());

    @Autowired
    @PersistenceContext
    EntityManager entityManager;

    @PostMapping(value = "/list")
    public ResponseEntity<?> selectAll(@RequestBody QueryBody item) {
        logger.info("[init] -------- [treatment] execute select to get response list");

        List<QueryReturn> result = new ArrayList<>();
        String query = SqlConstraints.SQL_LIST_TREATMENT + (!item.getLoggedUser().isEmpty() ? item.getLoggedUser() : 168)  + SqlConstraints.SQL_LIST_TREATMENT_WHERE_INTER + item.getWhereValue() + SqlConstraints.SQL_LIST_TREATMENT_GROUP;

        System.out.println(query);
        try {
            Query query1 = entityManager.createNativeQuery(query, QueryReturn.class);

            result = query1.unwrap(org.hibernate.query.Query.class)
                        .setTupleTransformer(
                                (ListResultTransformer)
                                        (tuple, aliases) -> new QueryReturn(aliases, tuple)
                        ).getResultList();

            logger.info("[finish] -------- [200] [List not empty] [treatment] execute select to get response list");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            if (result.isEmpty()) {
                HibernateUtil.shutdown();
                logger.info("[finish] -------- [500] [List empty] [treatment] execute select to get response list");
                return ResponseEntity.internalServerError().body("Result resolve in a empty List");
            } else {
                HibernateUtil.shutdown();
                logger.info("[finish] -------- [500] [treatment] execute select to get response list");
                return ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());
            }
        }
    }

    @PostMapping(name = "getEvolutionHistory", value = "/evolution")
    public ResponseEntity<?> selectEvolution(@RequestBody QueryBody item) {
        logger.info("[init] -------- [evolution] execute select to get response list");

        List<QueryReturn> result = new ArrayList<>();
        String query = SqlConstraints.SQL_EVOLUTION_HISTORY + item.getWhereValue() + " ORDER BY A.EVOLUCAO DESC, A.HORA DESC";
        try {
            Query query1 = entityManager.createNativeQuery(query, QueryReturn.class);

            result = query1.unwrap(org.hibernate.query.Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)
                    ).getResultList();

            logger.info("[finish] -------- [200] [List not empty] [evolution] execute select to get response list");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            if (result.isEmpty()) {
                HibernateUtil.shutdown();
                logger.info("[finish] -------- [500] [List empty] [evolution] execute select to get response list");
                return ResponseEntity.internalServerError().body("Result resolve in a empty List");
            } else {
                HibernateUtil.shutdown();
                logger.info("[finish] -------- [500] [evolution] execute select to get response list");
                return ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());
            }
        }
    }
}
