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

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "vw_informacao_saida_paciente")
public class InformacaoSaidaPaciente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "assigningAuthority", length = 40, nullable = false)
    private String assigningAuthority;

    @Column(name = "cnes", length = 40, nullable = false)
    private String cnes;

    @Column(name = "paciId", length = 64, nullable = false)
    private String paciId;

    @Column(name = "idAdmissao")
    private Long idAdmissao;

    @Column(name = "saidaDtAlta")
    private String saidaDtAlta;

    @Column(name = "saidaMotivo")
    private String saidaMotivo;

    @Column(name = "saidaDestino")
    private String saidaDestino;

    @Column(name = "saidaObservacao")
    private String saidaObservacao;

    @Column(name = "alobSq")
    private Long alobSq;

    @Column(name = "alobCprofSq")
    private Long alobCprofSq;

    @Column(name = "cproIdConselho")
    private String cproIdConselho;

    @Column(name = "cproSgConselho")
    private String cproSgConselho;

    @Column(name = "cproSgRegiaoConselho")
    private String cproSgRegiaoConselho;

    @Column(name = "cproNm")
    private String cproNm;

    @Column(name = "alobObStAntes48H")
    private String alobObStAntes48H;

    @Column(name = "alobAlSumarioAlta")
    private String alobAlSumarioAlta;

    @Column(name = "alobDtLiberacao")
    private String alobDtLiberacao;

    @Column(name = "alobLoginLiberacao")
    private String alobLoginLiberacao;

    @Column(name = "alobDtHr")
    private String alobDtHr;

    @Column(name = "alobObDtHr")
    private String alobObDtHr;

    @Column(name = "alobAlDtHrReal")
    private String alobAlDtHrReal;

    @Column(name = "saiTipo")
    private String saiTipo;

    @Column(name = "saiQualific")
    private String saiQualific;

    @Column(name = "saidSq")
    private Long saidSq;

    @Column(name = "saiTipoDest")
    private String saiTipoDest;

    @Column(name = "saiDestino")
    private String saiDestino;

    @Column(name = "saiDtHr")
    private String saiDtHr;

    @Column(name = "saiDtLiberacao")
    private String saiDtLiberacao;

    @Column(name = "saiDtSaida")
    private String saiDtSaida;

    @Column(name = "saidLogin")
    private String saidLogin;

    @Column(name = "saidDtHrReg")
    private String saidDtHrReg;

    @Column(name = "saidObs")
    private String saidObs;

    @Column(name = "id")
    private String id;

    @Column(name = "ATUALIZACAO")
    private String atualizacao;
}
