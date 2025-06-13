package br.com.savemed.model.auth;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "USUARIO")
@NoArgsConstructor
@EqualsAndHashCode
public class UserSave implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USUARIO", nullable = false)
    private Integer usuario;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "LOGON", nullable = false)
    private String logon;

    @Column(name = "SENHA", nullable = false)
    private String senha;

    @Column(name = "ATUALIZACAO", nullable = false)
    private String atualizacao;

    @Column(name = "ADMINISTRALICENCA")
    private Integer administraLicenca;

    @Column(name = "SNDI")
    private Integer sndi;

    @Column(name = "ASSINATURA")
    private Integer assigned;

    @Column(name = "SNDA")
    private Integer snda;

    @Column(name = "SNDE")
    private Integer snde;

    @Column(name = "ALMOXARIFADOI")
    private Integer almoxarifadoI;

    @Column(name = "ALMOXARIFADOA")
    private Integer almoxarifadoA;

    @Column(name = "ALMOXARIFADOE")
    private Integer almoxarifadoE;

    @Column(name = "FARMACIAI")
    private Integer farmaciaI;

    @Column(name = "FARMACIAA")
    private Integer farmaciaA;

    @Column(name = "FARMACIAE")
    private Integer farmaciaE;

    @Column(name = "ESTOQUEI")
    private Integer estoqueI;

    @Column(name = "ESTOQUEA")
    private Integer estoqueA;

    @Column(name = "ESTOQUEE")
    private Integer estoqueE;

    @Column(name = "EMAILPADRAOCOTACAO")
    private String emailPadraoCotacao;

    @Column(name = "PRESCRICAOINTERNO")
    private Integer prescricaoInterno;

    @Column(name = "PRESCRICAOAMBULATORIAL")
    private Integer prescricaoAmbulatorial;

    @Column(name = "PRESCRICAOURGENCIA")
    private Integer prescricaoUrgencia;

    @Column(name = "CCPADRAOCOTACAO")
    private Integer ccPadraoCotacao;

    @Column(name = "OBSERVACAO")
    private String observacao;

    @Column(name = "DESATIVADO")
    private Integer desativado;

    @Column(name = "TELAMEDICOAPENASCONSULTA")
    private Integer telaMedicoApenasConsulta;

    @Column(name = "ALTERAGUIATELATRANSFERENCIA")
    private Integer alteraGuiaTelaTransferencia;

    @Column(name = "ALTERA_TABELA_PRECO_CONVENIO")
    private Integer alteraTabelaPrecoConvenio;

    @Column(name = "PRESCRICAOEXTERNO")
    private Integer prescricaoExterno;

    @Column(name = "MEDICO_APENASCBO")
    private Integer medicoApenasCbo;

    @Column(name = "MEDICO_CADASTROESPECIALIDADE")
    private Integer medicoCadastroEspecialidade;

    @Column(name = "MEDICO_CADASTROCLASSIFICACAO")
    private Integer medicoCadastroClassificacao;

    @Column(name = "MEDICO_CADASTROHORARIO")
    private Integer medicoCadastroHorario;

    @Column(name = "MEDICO")
    private Integer medico;

    @Column(name = "TUTORPRESCRICAO")
    private Integer tutorPrescricao;

    @Column(name = "ADMINISTRACUSTO")
    private Integer administraCusto;

    @Column(name = "EXCLUIBAIXAFINANCEIROCOMPENSADO")
    private Integer excluiBaixaFinanceiroCompensado;

    @Column(name = "EXCLUIRAGENDACONSULTA")
    private Integer excluirAgendaConsulta;

    @Column(name = "PERMITEREEMPRIMIRPRESCRICAO")
    private Integer permiteReimprimirPrescricao;

    @Column(name = "PERMITEALTERANOME")
    private Integer permiteAlterarNome;

    @Column(name = "PREPARAKITMEDICAMENTO")
    private Integer preparaKitMedicamento;

    @Column(name = "PERMITE_ALTERAR_LANC_PAGTERCEIRO")
    private Integer permiteAlterarLancamentoPagTerceiro;

    @Column(name = "NAOPERMITEALTERARAGENDAMENTO")
    private Integer naoPermiteAlterarAgendamento;

    @Column(name = "REFERENCIA")
    private Integer referencia;

    @Column(name = "NAOPERMITEALTERACAOFINANCEIRO")
    private Integer naoPermiteAlteracaoFinanceiro;

    @Column(name = "UNIDADEENCAMINHAMENTO")
    private Integer unidadeEncaminhamento;

    @Column(name = "PERMITE_APENAS_LANCAR_CONTASRECEBER")
    private Integer permiteApenasLancarContasReceber;

    @Column(name = "PERMITE_CONFERIR_LAUDO")
    private Integer permiteConferirLaudo;

    @Column(name = "USUARIOUNIMED")
    private Integer usuarioUnimed;

    @Column(name = "GERENTE")
    private Integer gerente;

    @Column(name = "NAOPERMITECANCELARREGISTROS")
    private Integer naoPermiteCancelarRegistros;

    @Column(name = "PERMISSAOPREDEVOLUCAO")
    private Integer permissaoPreDevolucao;

    @Column(name = "PERMISSAODEVOLUCAO")
    private Integer permissaoDevolucao;

    @Column(name = "PERMITEDEVOLUCAODIRETA")
    private Integer permiteDevolucaoDireta;


    @Column(name = "RELPRESCSOMENTEDIETA")
    private Integer relPrescSomenteDieta;

    @Column(name = "CPF")
    private String cpf;

    @Column(name = "RG")
    private String rg;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATANASC")
    private Date dataNascimento;

    @Column(name = "INICIALIZARSENHA")
    private Integer inicializarSenha;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_ATUALIZACAO_SENHA")
    private Date dataAtualizacaoSenha;

    @Lob
    @Column(name = "LOGO")
    private byte[] logo;

    @Column(name = "RELCONTROLEPRONTUARIO")
    private Integer relControleProntuario;

    @Column(name = "PERMITEBAIXAMANUAL")
    private Integer permiteBaixaManual;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INICIO_VIGENCIA")
    private Date inicioVigencia;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FINAL_VIGENCIA")
    private Date finalVigencia;

    @Column(name = "PERMITE_ALTERAR_RASTREABILIDADE")
    private Integer permiteAlterarRastreabilidade;

    @Column(name = "SENHA_ORIGINAL")
    private String senhaOriginal;

    @Column(name = "NAO_INCLUIR_FATURA")
    private Integer naoIncluirFatura;

    @Column(name = "NAO_ALTERAR_FATURA")
    private Integer naoAlterarFatura;

    @Column(name = "NAO_EXCLUIR_FATURA")
    private Integer naoExcluirFatura;

    @Column(name = "NAO_AUDITAR_FATURA")
    private Integer naoAuditdarFatura;

    @Transient
    private String token;

    // MÉTODOS DA INTERFACE UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(); // Retorna uma lista vazia de permissões por enquanto
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.logon;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.desativado == null || this.desativado == 0;
    }

}
