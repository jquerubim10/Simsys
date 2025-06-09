package br.com.savemed.model.scheduler;

import br.com.savemed.model.enums.CanalType;
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
@Table(name = "ConfiguracaoNotificacao")
public class ConfiguracaoNotificacao implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ConfigID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long configID;

    @Enumerated(EnumType.STRING)
    @Column(name = "TipoEnvio")
    private CanalType tipoEnvio;

    @Column(name = "HorasAntesAgendamento")
    private Integer horasAntesAgendamento;

    @Column(name = "MensagemPadrao", columnDefinition = "NVARCHAR(MAX)")
    private String mensagemPadrao;

    @Column(name = "chip")
    private Integer chip;

    @Column(name = "enderecoHttp", columnDefinition = "NVARCHAR(MAX)")
    private String enderecoHttp;

    @Column(name = "token", columnDefinition = "NVARCHAR(MAX)")
    private String token;

    @Column(name = "instancia", columnDefinition = "NVARCHAR(MAX)")
    private String instancia;
}
