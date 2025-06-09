package br.com.savemed.model.message;

import br.com.savemed.dto.message.EntityReference;
import br.com.savemed.model.enums.*;
import br.com.savemed.model.scheduler.Agendamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Mensagens")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MensagemID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TipoCanal", nullable = false)
    private CanalType tipoCanal;

    @Enumerated(EnumType.STRING)
    @Column(name = "TipoMensagem", nullable = false)
    private TipoMensagem tipoMensagem;

    @Column(name = "Conteudo", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(name = "Direcao", nullable = false)
    private DirecaoType direcao;

    @Column(name = "DataHoraEnvio", nullable = false)
    private LocalDateTime dataHoraEnvio;

    @Column(name = "DataHoraResposta")
    private LocalDateTime dataHoraResposta;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private StatusMensagem status;

    @Column(name = "NumeroTelefone")
    private String numeroTelefone;

    @Column(name = "Email")
    private String email;

    @Column(name = "Assunto")
    private String assunto;

    @ManyToOne
    @JoinColumn(name = "MensagemPaiID")
    private Mensagem mensagemPai;

    @Column(name = "Anexos", columnDefinition = "XML")
    private String anexos;

    @Column(name = "Prioridade")
    private Integer prioridade;

    @Column(name = "CategoriaMensagem")
    private String categoriaMensagem;

    @Column(name = "ResponsavelEnvio")
    private String responsavelEnvio;

    @Column(name = "SistemaExternoID")
    private String sistemaExternoId;

    @Embedded
    private EntityReference entidadeRef;

    @Column(name = "Observacoes")
    private String observacoes;

    @Column(name = "ProviderResponse", columnDefinition = "NVARCHAR(MAX)")
    private String providerResponse;

    // Getter e Setter (se não estiver usando Lombok)
    public String getProviderResponse() {
        return providerResponse;
    }

    public void setProviderResponse(String providerResponse) {
        this.providerResponse = providerResponse;
    }

    public EntidadeType getEntidadeType() {
        return entidadeRef != null ? entidadeRef.getType() : null;
    }

    public Long getEntidadeId() {
        return entidadeRef != null ? entidadeRef.getId() : null;
    }
    public void setEntidadeType(EntidadeType type) {
        if (entidadeRef == null) {
            entidadeRef = new EntityReference();
        }
        entidadeRef.setType(type);
    }

    public void setEntidadeId(Long id) {
        if (entidadeRef == null) {
            entidadeRef = new EntityReference();
        }
        entidadeRef.setId(id);
    }
    public void setTipoCanal(CanalType tipoCanal) {
        this.tipoCanal = tipoCanal;
    }

    public void setNumeroTelefone(String numeroTelefone) {
        this.numeroTelefone = numeroTelefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Mensagem{" +
                "id=" + id +
                ", tipoCanal=" + tipoCanal +
                ", conteudo='" + conteudo + '\'' +
                ", direcao=" + direcao +
                ", dataHoraEnvio=" + dataHoraEnvio +
                ", numeroTelefone='" + numeroTelefone + '\'' +
                ", prioridade=" + prioridade +
                ", sistemaExternoId='" + sistemaExternoId + '\'' +
                ", entidadeType=" + entidadeRef.getType() +
                ", entidadeId='" + entidadeRef.getId() + '\'' +
                ", tipoMensagem='" + tipoMensagem + '\'' +
                ", dataHoraResposta=" + dataHoraResposta +
                ", providerResponse='" + providerResponse + '\'' +
                ", status=" + status +
                '}';
    }

}

