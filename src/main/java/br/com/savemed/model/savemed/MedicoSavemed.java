package br.com.savemed.model.savemed;


import jakarta.persistence.Column;
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
@Table(name = "MEDICOS")
public class MedicoSavemed {

    @Id
    private Long medico;

    @Column(name = "Nome")
    private String nome;
}


