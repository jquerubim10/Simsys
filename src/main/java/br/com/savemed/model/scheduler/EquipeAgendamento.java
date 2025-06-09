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
@Table(name = "EquipeAgendamento")
public class EquipeAgendamento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "EquipeAgendamentoID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AgendamentoID")
    private Long agendamentoID;

    @Column(name = "MedicoID")
    private Long medicoID;

    @Column(name = "Funcao")
    private String funcao;
}
