package br.com.savemed.controllers;

import br.com.savemed.config.HibernateUtil;
import br.com.savemed.controllers.interfaces.ListResultTransformer;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.query.QueryReturn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
