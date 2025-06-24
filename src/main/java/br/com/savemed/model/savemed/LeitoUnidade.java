package br.com.savemed.model.savemed;

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
@Table(name = "LEITOUNIDADE")
public class LeitoUnidade implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "LEITOUNIDADE")
    private Integer leitoUnidade;

    @Column(name = "LEITOTIPO")
    private Integer leitoTipo;

    @Column(name = "DESCRICAO")
    private String descricao;

    @Column(name = "ATUALIZACAO")
    private String atualizacao;

    @Column(name = "CENTROCUSTO")
    private Integer centroCusto;

    @Column(name = "RAMAL")
    private Integer ramal;

    @Column(name = "LOCALARMAZENAMENTO")
    private Integer localArmazenamento;

    @Column(name = "COR")
    private String cor;

    @Column(name = "SEXO")
    private Integer sexo;

    @Column(name = "SIGLA")
    private String sigla;

    @Column(name = "QUANTIDADELEITO")
    private Integer quantidadeLeito;

    @Column(name = "QUANTIDADEMAXIMAVISITANTE")
    private Integer quantidadeMaximaVisitante;

    @Column(name = "LEITOATIVOS")
    private Integer leitoAtivos;

    @Column(name = "OBSERVACAO_UNIDADE")
    private String observacaoUnidade;

    @Column(name = "QUANTIDADELEITOOUTROSCONVENIOS")
    private Integer quantidadeLeitoOutrosConvenios;

    @Column(name = "DESATIVAR")
    private Integer desativar;

    @Column(name = "USUARIORESPONSAVEL")
    private Integer usuarioResponsavel;

    @Column(name = "LEITOAMBULATORIAL")
    private Integer leitoAmbulatorial;
}