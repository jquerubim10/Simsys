package br.com.savemed.model.scheduler;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "PermissaoAgendamento")
public class PermissaoAgendamento {

    @Id
    @Column(name = "PermissaoID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SecretariaID")
    private Integer secretariaID;

    @Column(name = "MedicoID")
    private Integer medicoID;

    @Column(name = "CentroCustoID")
    private Integer centroCustoID;
}
