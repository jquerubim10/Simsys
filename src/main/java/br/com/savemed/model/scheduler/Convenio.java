package br.com.savemed.model.scheduler;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "CONVENIOS")
public class Convenio {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Convenio")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer convenio;

    @Column(name = "Descricao")
    private String descricao;
}
