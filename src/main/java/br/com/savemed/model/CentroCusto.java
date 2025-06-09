package br.com.savemed.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CentroCusto {

    private long id;
    private String descricao;
    private long perMedicamento;
}
