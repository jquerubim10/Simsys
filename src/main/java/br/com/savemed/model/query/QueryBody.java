package br.com.savemed.model.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryBody {

    private String operationType;
    private String columnName;
    private String tableName;
    private String valuesInsert;
    private String valuesUpdate;
    private String whereValue;
    private String secondWhereValue;
    private String tpTreatment;
    private String selectOne;
    private String tutorUser;
    private String loggedUser;
    private String arrayCenter;
    private String sqlValidation;
    private String valueScheduler;
    private Integer valueInsider;
    private Long valueLong;
    private Long valueMedico;
}
