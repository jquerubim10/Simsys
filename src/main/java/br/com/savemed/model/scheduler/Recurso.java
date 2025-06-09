package br.com.savemed.model.scheduler;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "Recurso")
public class Recurso implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "RecursoID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "Nome")
    private String nome;

    @Column(name = "TipoRecurso")
    private String tipoRecurso;

    @NotNull
    @Column(name = "CentroCustoID")
    private Long centroCustoId;

    @Column(name = "Ativo", columnDefinition = "boolean default true")
    private boolean ativo;
}

//CREATE TABLE Recurso (
//        RecursoID INT PRIMARY KEY IDENTITY,
//        codigo NVARCHAR(100) NOT NULL,
//        Nome NVARCHAR(800) NOT NULL,
//        TipoRecurso NVARCHAR(50) NOT NULL, -- Sala, Equipamento, Outro,Exame
//        CentroCustoID smallint NOT NULL,
//        Ativo BIT DEFAULT 1,
//);
