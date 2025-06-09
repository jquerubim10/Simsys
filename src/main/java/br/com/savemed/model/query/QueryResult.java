package br.com.savemed.model.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryResult {

    private String code;
    private String message;
    private Object returnID;

    public QueryResult(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
