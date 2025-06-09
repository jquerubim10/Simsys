package br.com.savemed.model.ws;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "vw_informacao_historico_leitos")
public class InformacaoHistoricoLeitos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "assigningAuthority", length = 40, nullable = false)
    private String assigningAuthority;

    @Column(name = "unfuSigla")
    private String unfuSigla;

    @Column(name = "unfuNome")
    private String unfuNome;

    @Column(name = "unfuNomeReduz")
    private String unfuNomeReduz;

    @Column(name = "cnes", length = 40)
    private String cnes;

    @Column(name = "leitoId", length = 64, nullable = false)
    private String leitoId;

    @Column(name = "leitoSt",  nullable = false)
    private String leitoSt;

    @Column(name = "leitoTp",  nullable = false)
    private String leitoTp;

    @Column(name = "situacao")
    private String situacao;

    @Column(name = "motivoBloqueio")
    private String motivoBloqueio;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "paciId", length = 64, nullable = false)
    private String paciId;

    @Column(name = "acomodacao")
    private String acomodacao;

    @Column(name = "tipoAcomodacao")
    private String tipoAcomodacao;

    @Column(name = "dtHrInicioReserva")
    private String dtHrInicioReserva;

    @Column(name = "dtHrFimReserva")
    private String dtHrFimReserva;

    @Column(name = "paciIdReserva")
    private String paciIdReserva;

    @Column(name = "idAdmissao")
    private String idAdmissao;

    @Column(name = "dtHrInicio")
    private String dtHrInicio;

    @Column(name = "dtHrFim")
    private String dtHrFim;

    @Column(name = "dtHrCancel")
    private String dtHrCancel;

    @Column(name = "id")
    private String id;

    @Column(name = "ATUALIZACAO")
    private String atualizacao;
}
