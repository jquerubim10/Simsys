package br.com.savemed.controllers;

import br.com.savemed.config.HibernateUtil;
import br.com.savemed.controllers.interfaces.ListResultTransformer;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.query.QueryReturn;
import br.com.savemed.util.SqlConstraints;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@SuppressWarnings({"unchecked", "unsafe"})
@RequestMapping("/api/v1/tutor")
public class TutorController {
    private final Logger logger = Logger.getLogger(TutorController.class.getName());

    @Autowired
    @PersistenceContext
    EntityManager entityManager;

    @GetMapping(value = "/list")
    public ResponseEntity<?> getAll() {
        logger.info("[init] -------- [tutor] execute select to get response list");

        List<QueryReturn> result = List.of();
        try {
            jakarta.persistence.Query query1 = entityManager.createNativeQuery(SqlConstraints.SQL_TUTOR_LIST, QueryReturn.class);

            result = query1.unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)
                    ).getResultList();

            HibernateUtil.shutdown();
            logger.info("[finish] -------- [200] [tutor] execute select to get response list");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            if (result.isEmpty()) {
                HibernateUtil.shutdown();
                logger.info("[finish] -------- [500] [tutor] execute select to get response list");
                return ResponseEntity.internalServerError().body("Result resolve in a empty List");
            } else {
                HibernateUtil.shutdown();
                logger.info("[finish] -------- [500] [tutor] execute select to get response list");
                return ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());
            }
        }
    }
}
