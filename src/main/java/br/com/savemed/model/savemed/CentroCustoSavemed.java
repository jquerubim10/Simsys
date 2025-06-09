package br.com.savemed.model.savemed;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "CENTROCUSTO")
public class CentroCustoSavemed {

    @Id
    private Long centrocusto;

    private String descricao;
}
