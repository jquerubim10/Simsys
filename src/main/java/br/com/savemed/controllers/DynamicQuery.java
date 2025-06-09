package br.com.savemed.controllers;

import br.com.savemed.config.HibernateUtil;
import br.com.savemed.controllers.interfaces.ListResultTransformer;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.query.QueryResult;
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
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

@RestController
@SuppressWarnings({"unchecked", "unsafe"})
@RequestMapping("/api/v1/query")
@Tag(name = "Builder Screen", description = "Endpoints for Managing Builder Screen")
public class DynamicQuery {
    private final Logger logger = Logger.getLogger(DynamicQuery.class.getName());

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

    @Autowired
    @PersistenceContext
    EntityManager entityManager;

    @PostMapping(value = "/doctor/find", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findDoctorsByName(@RequestBody QueryBody item) {
        logger.info("init -------- execute select to get doctors");

        configureDatabase();

        List<QueryReturn> result = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            String query =  SqlConstraints.SQL_DOCTOR_FIND_PART_1 + item.getWhereValue() + SqlConstraints.SQL_DOCTOR_FIND_PART_2;

            result = (List<QueryReturn>) session.createNativeQuery(query, QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            logger.info("finish -------- [200] [List not empty] execute select to get doctors");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return getStringResponseEntity(result, "finish -------- [500] [List empty] execute select to get doctors", "finish -------- [500] execute select to get doctors", e);
        }
    }

    @PostMapping(value = "/maps", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findMaps(@RequestBody QueryBody item) {
        logger.info("init -------- execute select to get maps");

        configureDatabase();

        List<QueryReturn> result = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            String query =  SqlConstraints.SQL_MAPS_SCHEDULER + item.getValueScheduler();

            result = (List<QueryReturn>) session.createNativeQuery(query, QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            logger.info("finish -------- [200] execute select to get maps");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return getStringResponseEntity(result,
                    "finish -------- [500] [List empty] execute select to get maps",
                    "finish -------- [500] execute select to get maps", e);
        }
    }


    @PostMapping(value = "/grade/hours", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllHoursGrade(@RequestBody QueryBody item) {
        logger.info("init -------- execute select to get grade hours");

        configureDatabase();

        List<QueryReturn> result = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            String query =  SqlConstraints.SQL_DATE_INIT + item.getWhereValue() +
                            SqlConstraints.SQL_DATE_CENTRO + item.getValueLong() +
                            SqlConstraints.SQL_DATE_MEDICO + item.getValueMedico() +
                            SqlConstraints.SQL_DATA_SECOND + " AND 0 = " + item.getValueInsider() +
                            " AND CAST(A.HoraInicio AS DATE) = '" + item.getWhereValue() + "' \n" +
                            " AND A.MedicoID = " + item.getValueMedico() +
                            item.getValueScheduler() +
                           " \n AND LTRIM(RTRIM(A.STATUS)) NOT IN('cancelada_pelo_paciente','cancelada_pelo_profissional'))" +
                           " \n ORDER BY I.Horario";

            result = (List<QueryReturn>) session.createNativeQuery(query, QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            logger.info("finish -------- [200] [List not empty] execute select to get grade hours");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return getStringResponseEntity(result,
                    "finish -------- [500] [List empty] execute select to get grade hours",
                    "finish -------- [500] execute select to get grade hours",
                    e);
        }
    }

    @PostMapping(value = "/validate/scheduler", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateToSaveScheduler(@RequestBody QueryBody item) {
        logger.info("init -------- execute select to validate scheduler");

        configureDatabase();

        List<QueryReturn> result = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();
            result = (List<QueryReturn>) session.createNativeQuery(item.getSqlValidation(), QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            logger.info("finish -------- [200] execute select to validate scheduler");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return getStringResponseEntity(result, "finish -------- [500] execute select to validate scheduler", "finish -------- [500] execute select to validate scheduler", e);
        }
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> selectAll(@RequestBody QueryBody item) {
        logger.info("init -------- execute select to get response list");

        configureDatabase();

        List<QueryReturn> result = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            String query = item.getOperationType().toUpperCase() +
                    item.getColumnName().toUpperCase() +
                    " FROM " +
                    item.getTableName().toUpperCase() +
                    " ORDER BY 1 DESC OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY";

            result = (List<QueryReturn>)session.createNativeQuery(query, QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            logger.info("finish -------- [200] [List not empty] execute select to get response list");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return getStringResponseEntity(result, "finish -------- [500] [List empty] execute select to get response list", "finish -------- [500] execute select to get response list", e);
        }
    }

    @PostMapping(value = "/list/modal", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<?>> selectDynamicList(@RequestBody QueryBody item) {

        logger.info("init -------- execute query with modal");

        configureDatabase();

        List<QueryReturn> result = null;
        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            String query = item.getOperationType().toUpperCase() +
                    item.getColumnName().toUpperCase() +
                    " FROM " +
                    item.getTableName().toUpperCase() +
                    " " +
                    item.getWhereValue().toUpperCase();


            result = (List<QueryReturn>)session.createNativeQuery(query, QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            logger.info("finish -------- execute query with modal");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            if (result != null) {
                logger.info("finish -------- execute query with modal");
                return ResponseEntity.ok().build();
            } else {
                logger.info("finish -------- dynamic insert");
                ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());
            }
        }

        HibernateUtil.shutdown();
        logger.info("finish -------- dynamic insert");
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/list/modal/query", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> selectDynamicListQuery(@RequestBody QueryBody item) {

        logger.info("init -------- execute query with modal by query");

        configureDatabase();

        List<QueryReturn> result = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();


            result = (List<QueryReturn>)session.createNativeQuery(item.getSelectOne(), QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            logger.info("finish -------- [200] [List not empty] execute query with modal by query");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return getStringResponseEntity(result, "finish -------- [500] [List empty] execute query with modal by query", "finish -------- [500] execute query with modal by query", e);
        }
    }

    @PostMapping(value = "/builderField", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<?>> select(@RequestBody QueryBody item) {

        configureDatabase();


        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();
            return ResponseEntity.ok((List<QueryReturn>)
                    session.createNativeQuery(item.getSelectOne(), QueryReturn.class)
                            .unwrap(Query.class)
                            .setTupleTransformer(
                                    (ListResultTransformer)
                                            (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList());
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        HibernateUtil.shutdown();
        return null;
    }

    @PostMapping(value = "/search/field", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<?>> searchField(@RequestBody QueryBody item) {

        logger.info("init -------- execute query with field search");

        configureDatabase();


        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();
            return ResponseEntity.ok((List<QueryReturn>)
                    session.createNativeQuery(item.getSelectOne(), QueryReturn.class)
                            .unwrap(Query.class)
                            .setTupleTransformer(
                                    (ListResultTransformer)
                                            (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList());
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        HibernateUtil.shutdown();
        logger.info("finish -------- execute query with field search");
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/lastId()", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleLastID() {
        logger.info("init -------- execute getLastId()");

        configureDatabase();

        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            // Construindo a query de inserção
            String query = "SELECT SCOPE_IDENTITY() AS LAST_ID";

            // Executa o INSERT
            List<?> result = (List<QueryReturn>)session.createNativeQuery(query, QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            // Comita a transação
            session.getTransaction().commit();
            HibernateUtil.shutdown();

            logger.info("finish -------- [200] execute getLastId()");

            // Retorna o scopeId na resposta
            QueryResult query_result = new QueryResult("200", "success", result);
            return ResponseEntity.created(URI.create("created")).body(query_result);
        } catch (Exception e) {
            if (!((SQLGrammarException) e).getSQLException().getSQLState().equals("S0001")) {
                HibernateUtil.shutdown();
                logger.info("finish -------- [200] execute getLastId()");
                return ResponseEntity.ok(null);
            }

            HibernateUtil.shutdown();
            logger.info("finish -------- [500] execute getLastId()");
            return ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());

        }
    }

    @PostMapping(value = "/post/sql/execute", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handlePostSql(@RequestBody QueryBody item) {

        logger.info("init -------- execute query postSave");

        configureDatabase();

        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            // Construindo a query de inserção
            String query = item.getSelectOne() + "SELECT SCOPE_IDENTITY() AS LAST_ID";

            // Executa o INSERT
            List<?> result = (List<QueryReturn>)session.createNativeQuery(query, QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            // Comita a transação
            session.getTransaction().commit();
            HibernateUtil.shutdown();

            logger.info("finish -------- [200] execute query postSave");

            // Retorna o scopeId na resposta
            QueryResult query_result = new QueryResult("200", "success", result);
            return ResponseEntity.created(URI.create("created")).body(query_result);
        } catch (Exception e) {
            if (!((SQLGrammarException) e).getSQLException().getSQLState().equals("S0001")) {
                HibernateUtil.shutdown();
                logger.info("finish -------- [200] execute query postSave");
                return ResponseEntity.ok(null);
            }

            HibernateUtil.shutdown();
            logger.info("finish -------- [500] execute query postSave");
            return ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());

        }
    }

    @PostMapping(value = "/preOrUp/execute", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> preSaveOrUpdate(@RequestBody QueryBody item) {

        logger.info("init -------- execute query preSave or Update");

        configureDatabase();

        List<QueryReturn> result = null;

        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

             result = (List<QueryReturn>)session.createNativeQuery(item.getSelectOne(), QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            HibernateUtil.shutdown();
            logger.info("finish -------- [200] execute query preSave or Update");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            if (!((SQLGrammarException) e).getSQLException().getSQLState().equals("S0001")) {
                HibernateUtil.shutdown();
                logger.info("finish -------- [200] execute query preSave or Update");
                return ResponseEntity.ok(result);
            }

            HibernateUtil.shutdown();
            logger.info("finish -------- [500] execute query preSave or Update");
            return ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());

        }
    }

    @PostMapping(value = "/one")
    public ResponseEntity<List<?>> selectOne(@RequestBody QueryBody item) {
        configureDatabase();


        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            String query = item.getOperationType().toUpperCase() +
                    " TOP 1 * FROM " +
                    item.getTableName().toUpperCase() +
                    " WHERE " + item.getWhereValue().toUpperCase();

            return ResponseEntity.ok((List<QueryReturn>)
                    session.createNativeQuery(query, QueryReturn.class)
                            .unwrap(Query.class)
                            .setTupleTransformer(
                                    (ListResultTransformer)
                                            (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList());
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        HibernateUtil.shutdown();
        return null;
    }

    @PostMapping(name = "dynamic insert", value = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> dynamicInsert(@RequestBody QueryBody item) {
        logger.info("init -------- method dynamic insert");

        configureDatabase();

        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            // Construindo a query de inserção
            String query = item.getOperationType().toUpperCase() +
                    " INTO " + item.getTableName().toUpperCase() +
                    "(" + item.getColumnName().toUpperCase() + ") VALUES (" + item.getValuesInsert() + ") \n" +
                    "SELECT SCOPE_IDENTITY() AS LAST_ID";


            // Executa o INSERT
            List<?> result = (List<QueryReturn>)session.createNativeQuery(query, QueryReturn.class)
                    .unwrap(Query.class)
                    .setTupleTransformer(
                            (ListResultTransformer)
                                    (tuple, aliases) -> new QueryReturn(aliases, tuple)).getResultList();

            // Comita a transação
            session.getTransaction().commit();
            HibernateUtil.shutdown();

            logger.info("finish -------- [201] method dynamic insert with");

            // Retorna o scopeId na resposta
            QueryResult queryR = new QueryResult("201", "created", result);
            return ResponseEntity.created(URI.create("created")).body(queryR);

        } catch (Exception e) {
            if (e.getCause().getMessage().toLowerCase().contains("a result")) {
                HibernateUtil.shutdown();
                QueryResult query = new QueryResult("201", "created");
                logger.info("finish -------- [201] method dynamic insert");
                return ResponseEntity.created(URI.create("created")).body(query);
            } else {
                HibernateUtil.shutdown();
                logger.info("finish -------- [500] method dynamic insert");
                return ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());
            }
        }
    }

    @PostMapping(name = "dynamic update", value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> dynamicUpdate(@RequestBody QueryBody item) {
        logger.info("init -------- method dynamic update");

        configureDatabase();

        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            String query = item.getOperationType().toUpperCase()
                    + item.getTableName().toUpperCase() +
                    " SET " + item.getValuesUpdate() + " WHERE " + item.getWhereValue().toUpperCase();

            session.createNativeQuery(query, QueryReturn.class).executeUpdate();

            HibernateUtil.shutdown();
            logger.info("finish -------- [204] method dynamic update");
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            if (!((SQLGrammarException) e).getSQLException().getSQLState().equals("S0001")) {
                HibernateUtil.shutdown();
                logger.info("finish -------- [204] method dynamic update");
                return ResponseEntity.noContent().build();
            } else {
                HibernateUtil.shutdown();
                logger.info("finish -------- [500] method dynamic update");
                return ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());
            }
        }
    }

    @PostMapping(name = "dynamic delete", value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> dynamicDelete(@RequestBody QueryBody item) {
        logger.info("init -------- method dynamic delete");

        configureDatabase();

        try (Session session = HibernateUtil.getSessionFactory(settings).openSession()) {
            session.beginTransaction();

            String query = item.getOperationType().toUpperCase()
                    + " FROM " +item.getTableName().toUpperCase()
                    + " WHERE " + item.getWhereValue().toUpperCase();

            session.createNativeQuery(query, QueryReturn.class).executeUpdate();

            HibernateUtil.shutdown();
            logger.info("finish -------- [200] method dynamic insert");
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            if (!((SQLGrammarException) e).getSQLException().getSQLState().equals("S0001")) {
                HibernateUtil.shutdown();
                logger.info("finish -------- [200] method dynamic delete");
                return ResponseEntity.ok().build();
            } else {
                HibernateUtil.shutdown();
                logger.info("finish -------- [500] method dynamic delete");
                return ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());
            }
        }
    }

    @GetMapping(value = "last/id", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find last id insert", description = "Find last id insert", tags = {"preview"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<?> getLastIdInsert() {
        jakarta.persistence.Query query1 = entityManager.createNativeQuery("SELECT SCOPE_IDENTITY() AS NovoID", QueryReturn.class);

        List<?> result = query1.unwrap(org.hibernate.query.Query.class)
                .setTupleTransformer(
                        (ListResultTransformer)
                                (tuple, aliases) -> new QueryReturn(aliases, tuple)
                ).getResultList();

        return ResponseEntity.ok(result);
    }

    private void configureDatabase() {
        settings.put(Environment.DRIVER, driver);
        settings.put(Environment.URL, url);
        settings.put(Environment.USER, user);
        settings.put(Environment.PASS, password);
        settings.put(Environment.SHOW_SQL, true);
    }

    private ResponseEntity<?> getStringResponseEntity(List<QueryReturn> result, String msg, String msg1, Exception e) {
        if (result.isEmpty()) {
            HibernateUtil.shutdown();
            logger.info(msg);
            return ResponseEntity.ok().body(List.of());
        } else {
            HibernateUtil.shutdown();
            logger.info(msg1);
            return ResponseEntity.internalServerError().body(e.getCause().getLocalizedMessage());
        }
    }

}
