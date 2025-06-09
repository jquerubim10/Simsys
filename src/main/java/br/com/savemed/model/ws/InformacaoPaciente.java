package br.com.savemed.model.ws;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "vw_informacao_paciente")
public class InformacaoPaciente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "assigningAuthority", length = 40, nullable = false)
    private String assigningAuthority;

    @Column(name = "cnes", length = 40)
    private String cnes;

    @Column(name = "paciId", length = 64, nullable = false)
    private String paciId;

    @Column(name = "unfuSigla", length = 20)
    private String unfuSigla;

    @Column(name = "unfuNome", length = 240)
    private String unfuNome;

    @Column(name = "unfuNomeReduz", length = 60)
    private String unfuNomeReduz;

    @Column(name = "matriculaProntuario", length = 64)
    private String matriculaProntuario;

    @Column(name = "nome", length = 240)
    private String nome;

    @Column(name = "nomeSocial", length = 240)
    private String nomeSocial;

    @Column(name = "dtNasc")
    private LocalDate dtNasc;

    @Column(name = "tpSexo", length = 1)
    private String tpSexo;

    @Column(name = "sexo", length = 20)
    private String sexo;

    @Column(name = "origem", length = 20)
    private String origem;

    @Column(name = "uf", length = 50)
    private String uf;

    @Column(name = "muniSq")
    private Integer muniSq;

    @Column(name = "muniDs", length = 50)
    private String muniDs;

    @Column(name = "naturalidade", length = 50)
    private String naturalidade;

    @Column(name = "mae", length = 240)
    private String mae;

    @Column(name = "pai", length = 240)
    private String pai;

    @Column(name = "conjuge", length = 240)
    private String conjuge;

    @Column(name = "responsavel", length = 240)
    private String responsavel;

    @Column(name = "padeGretSq")
    private Integer padeGretSq;

    @Column(name = "etnia", length = 20)
    private String etnia;

    @Column(name = "padeEtdeSq")
    private Integer padeEtdeSq;

    @Column(name = "etniaDetalhe", length = 40)
    private String etniaDetalhe;

    @Column(name = "padeReliSq")
    private Integer padeReliSq;

    @Column(name = "religiao", length = 20)
    private String religiao;

    @Column(name = "padeEsciSq")
    private Integer padeEsciSq;

    @Column(name = "estadoCivil", length = 20)
    private String estadoCivil;

    @Column(name = "padeOcupSq")
    private Integer padeOcupSq;

    @Column(name = "ocupacao", length = 50)
    private String ocupacao;

    @Column(name = "padeSifaSq")
    private Integer padeSifaSq;

    @Column(name = "situacaoFamiliar", length = 50)
    private String situacaoFamiliar;

    @Column(name = "padeEcldSq")
    private Integer padeEcldSq;

    @Column(name = "escolaridade", length = 50)
    private String escolaridade;

    @Column(name = "padeStFreqEsco", length = 1)
    private String padeStFreqEsco;

    @Column(name = "endeTpEndereco", length = 20)
    private String endeTpEndereco;

    @Column(name = "endeTienSq")
    private Integer endeTienSq;

    @Column(name = "endeCep", length = 20)
    private String endeCep;

    @Column(name = "endeTiloCd", length = 10)
    private String endeTiloCd;

    @Column(name = "endeTpLogradouro", length = 20)
    private String endeTpLogradouro;

    @Column(name = "endeNmLogr", length = 50)
    private String endeNmLogr;

    @Column(name = "endeNrImovel", length = 20)
    private String endeNrImovel;

    @Column(name = "endeComplemento", length = 50)
    private String endeComplemento;

    @Column(name = "endePaisSq")
    private Integer endePaisSq;

    @Column(name = "endePaisDs", length = 20)
    private String endePaisDs;

    @Column(name = "endeUf", length = 5)
    private String endeUf;

    @Column(name = "endeUnfeDs", length = 240)
    private String endeUnfeDs;

    @Column(name = "endeMuniSq")
    private Integer endeMuniSq;

    @Column(name = "endeMuniDs", length = 240)
    private String endeMuniDs;

    @Column(name = "endeMuniCdIbge", length = 20)
    private String endeMuniCdIbge;

    @Column(name = "endeDistSq")
    private Integer endeDistSq;

    @Column(name = "endeDistDs", length = 240)
    private String endeDistDs;

    @Column(name = "endeNmBairro", length = 40)
    private String endeNmBairro;

    @Column(name = "endeDsPontoRef", length = 240)
    private String endeDsPontoRef;

    @Column(name = "id")
    private String id;

    @Column(name = "ATUALIZACAO")
    private String atualizacao;
}
