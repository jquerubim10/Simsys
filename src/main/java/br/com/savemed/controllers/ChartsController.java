package br.com.savemed.controllers;

import br.com.savemed.config.HibernateUtil;
import br.com.savemed.controllers.interfaces.ListResultTransformer;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.query.QueryChart;
import br.com.savemed.model.query.QueryReturn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@SuppressWarnings({"unchecked", "unsafe"})
@RequestMapping("/api/v1/charts")
@Tag(name = "Charts", description = "Endpoints for Managing Charts")
public class ChartsController {

    private final Logger logger = Logger.getLogger(ChartsController.class.getName());

    @Value("${spring.datasource.driver-class-name}")
    String driver;

    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String user;

    @Value("${spring.datasource.password}")
    String password;

    @Value("${spring.jpa.database}")
    String type_base;

    // Configuration properties
    Map<String, Object> settings = new HashMap<>();

    @PostMapping(value = "/mov")
    public ResponseEntity<List<?>> selectByChartMov(@RequestBody QueryChart item) {

        logger.info("init -------- execute query chart movimento");

        settings.put(Environment.DRIVER, driver);
        settings.put(Environment.URL, url);
        settings.put(Environment.USER, user);
        settings.put(Environment.PASS, password);
        settings.put(Environment.SHOW_SQL, true);

        List<QueryReturn> result = null;

        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();


            String query = " SELECT DATA+HORA AS HOURDATA, " + item.getTableName() +
                            " FROM PEP_MONITORAMENTO WHERE " + item.getTableName() +
                            " IS NOT NULL AND " + item.getDateRange();

            result = (List<QueryReturn>)session.createNativeQuery(query, QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            if (result != null) {
                logger.info("finish -------- execute query chart movimento");
                HibernateUtil.shutdown();
                return ResponseEntity.ok().build();
            } else {
                logger.info("finish -------- execute query chart movimento");
                ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());
            }
        }

        HibernateUtil.shutdown();
        logger.info("finish -------- execute query chart movimento");
        return ResponseEntity.ok().build();
    }
    @PostMapping(value = "/signal/vital")
    public ResponseEntity<List<?>> selectByChartSignalVital(@RequestBody QueryChart item) {

        logger.info("init -------- execute query chart signal vital");

        settings.put(Environment.DRIVER, driver);
        settings.put(Environment.URL, url);
        settings.put(Environment.USER, user);
        settings.put(Environment.PASS, password);
        settings.put(Environment.SHOW_SQL, true);

        List<QueryReturn> result = null;

        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();


            String query = " SELECT DATA+HORA AS HOURDATA, " + item.getTableName() + " FROM SSVV WHERE " + item.getDateRange();

            result = (List<QueryReturn>)session.createNativeQuery(query, QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            if (result != null) {
                logger.info("finish -------- execute query chart signal vital");
                HibernateUtil.shutdown();
                return ResponseEntity.ok().build();
            } else {
                logger.info("finish -------- execute query chart signal vital");
                ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());
            }
        }

        HibernateUtil.shutdown();
        logger.info("finish -------- execute query chart signal vital");
        return ResponseEntity.ok().build();
    }
}
