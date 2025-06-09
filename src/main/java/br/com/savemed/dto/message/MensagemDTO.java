package br.com.savemed.dto.message;

import br.com.savemed.model.enums.*;
import br.com.savemed.model.message.Mensagem;

import java.time.LocalDateTime;

public class MensagemDTO {

    private Long id;
    private EntidadeType entidadeType;
    private Long entidadeId;
    private CanalType tipoCanal;
    private TipoMensagem tipoMensagem;
    private String conteudo;
    private DirecaoType direcao;
    private LocalDateTime dataHoraEnvio;
    private StatusMensagem status;
    private String numeroTelefone;
    private String email;
    private String assunto;
    private Long mensagemPaiId;

    public MensagemDTO(Long id, EntidadeType entidadeType, Long entidadeId, CanalType tipoCanal,
                       TipoMensagem tipoMensagem, String conteudo, DirecaoType direcao, LocalDateTime dataHoraEnvio,
                       StatusMensagem status, String numeroTelefone, String email, String assunto, Long mensagemPaiId) {
        this.id = id;
        this.entidadeType = entidadeType;
        this.entidadeId = entidadeId;
        this.tipoCanal = tipoCanal;
        this.tipoMensagem = tipoMensagem;
        this.conteudo = conteudo;
        this.direcao = direcao;
        this.dataHoraEnvio = dataHoraEnvio;
        this.status = status;
        this.numeroTelefone = numeroTelefone;
        this.email = email;
        this.assunto = assunto;
        this.mensagemPaiId = mensagemPaiId;
    }

    // Factory method: Entity -> DTO
    public static MensagemDTO fromEntity(Mensagem entity) {
        return new MensagemDTO(
                entity.getId(),
                entity.getEntidadeType(),
                entity.getEntidadeId(),
                entity.getTipoCanal(),
                entity.getTipoMensagem(),
                entity.getConteudo(),
                entity.getDirecao(),
                entity.getDataHoraEnvio(),
                entity.getStatus(),
                entity.getNumeroTelefone(),
                entity.getEmail(),
                entity.getAssunto(),
                (entity.getMensagemPai() != null && entity.getMensagemPai().getId() != 0) ? entity.getMensagemPai().getId() : null
        );
    }

    // Converter DTO -> Entity
    public Mensagem toEntity() {
        Mensagem mensagem = new Mensagem();
        mensagem.setEntidadeType(entidadeType);
        mensagem.setEntidadeId(entidadeId);
        mensagem.setTipoCanal(tipoCanal);
        mensagem.setTipoMensagem(tipoMensagem);
        mensagem.setConteudo(conteudo);
        mensagem.setDirecao(direcao);
        mensagem.setDataHoraEnvio(dataHoraEnvio);
        mensagem.setStatus(status);
        mensagem.setNumeroTelefone(numeroTelefone);
        mensagem.setEmail(email);
        mensagem.setAssunto(assunto);

        if (mensagemPaiId != null && mensagemPaiId != 0) {
            Mensagem pai = new Mensagem();
            pai.setId(mensagemPaiId);
            mensagem.setMensagemPai(pai);
        }

        return mensagem;
    }

    // Getters
    public Long getId() { return id; }

    public EntidadeType getEntidadeType() { return entidadeType; }

    public Long getEntidadeId() { return entidadeId; }

    public CanalType getTipoCanal() { return tipoCanal; }

    public TipoMensagem getTipoMensagem() { return tipoMensagem; }

    public String getConteudo() { return conteudo; }

    public DirecaoType getDirecao() { return direcao; }

    public LocalDateTime getDataHoraEnvio() { return dataHoraEnvio; }

    public StatusMensagem getStatus() { return status; }

    public String getNumeroTelefone() { return numeroTelefone; }

    public String getEmail() { return email; }

    public String getAssunto() { return assunto; }

    public Long getMensagemPaiId() { return mensagemPaiId; }
}
