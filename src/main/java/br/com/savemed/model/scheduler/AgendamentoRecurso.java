package br.com.savemed.model.scheduler;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "AgendamentoRecurso")
public class AgendamentoRecurso implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "AgendamentoRecursoID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AgendamentoID")
    private Long agendamentoID;

    @Column(name = "RecursoID")
    private Integer recursoID;

    @Column(name = "Quantidade")
    private Integer quantidade;

    public AgendamentoRecurso(Long agendamentoID, Integer recursoID, Integer quantidade) {
        this.agendamentoID = agendamentoID;
        this.recursoID = recursoID;
        this.quantidade = quantidade;
    }
}

/**
CREATE TABLE AgendamentoRecurso (
        AgendamentoRecursoID INT PRIMARY KEY IDENTITY,
        AgendamentoID INT NOT NULL FOREIGN KEY REFERENCES Agendamento(AgendamentoID),
        RecursoID INT NOT NULL FOREIGN KEY REFERENCES Recurso(RecursoID),
        Quantidade INT DEFAULT 1 -- Quantidade de unidades do recurso usado
);
 */