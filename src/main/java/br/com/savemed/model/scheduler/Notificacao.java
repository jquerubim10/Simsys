package br.com.savemed.model.scheduler;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "Notificacao", indexes = {
        @Index(name = "idx_notificacao_data_envio", columnList = "DataEnvio"),
        @Index(name = "idx_notificacao_status", columnList = "StatusEnvio")
})
public class Notificacao implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum MeioEnvio {
        SMS, WHATSAPP, EMAIL
    }

    public enum StatusEnvio {
        ENVIADO, FALHA, PENDENTE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NotificacaoID")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AgendamentoID", nullable = false)
    private Agendamento agendamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "MeioEnvio", length = 20)
    private MeioEnvio meioEnvio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "StatusEnvio", length = 50)
    private StatusEnvio statusEnvio;

    @PastOrPresent
    @Column(name = "DataEnvio")
    private LocalDateTime dataEnvio;

    @Lob
    @Column(name = "Resposta")
    private String resposta;

    @NotNull
    @Column(name = "Tentativas")
    private Integer tentativas = 1;

    // Builder Pattern
    public static NotificacaoBuilder builder() {
        return new NotificacaoBuilder();
    }

    public static class NotificacaoBuilder {
        private final Notificacao notificacao = new Notificacao();

        public NotificacaoBuilder agendamento(Agendamento agendamento) {
            notificacao.agendamento = agendamento;
            return this;
        }

        public NotificacaoBuilder meioEnvio(MeioEnvio meio) {
            notificacao.meioEnvio = meio;
            return this;
        }

        public NotificacaoBuilder statusEnvio(StatusEnvio status) {
            notificacao.statusEnvio = status;
            return this;
        }

        public NotificacaoBuilder resposta(String resposta) {
            notificacao.resposta = resposta;
            return this;
        }

        public NotificacaoBuilder tentativas(Integer tentativas) {
            if(tentativas != null) notificacao.tentativas = tentativas;
            return this;
        }

        public Notificacao build() {
            notificacao.dataEnvio = LocalDateTime.now();
            return notificacao;
        }
    }
}