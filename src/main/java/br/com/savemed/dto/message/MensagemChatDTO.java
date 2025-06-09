package br.com.savemed.dto.message;

import br.com.savemed.model.enums.DirecaoType;
import br.com.savemed.model.enums.StatusMensagem;
import br.com.savemed.model.message.Mensagem;

import java.time.LocalDateTime;

public class MensagemChatDTO {

    private Long id;
    private String conteudo;
    private DirecaoType direcao;
    private LocalDateTime dataHora;
    private StatusMensagem status;

    public MensagemChatDTO(Long id, String conteudo, DirecaoType direcao, LocalDateTime dataHora, StatusMensagem status) {
        this.id = id;
        this.conteudo = conteudo;
        this.direcao = direcao;
        this.dataHora = dataHora;
        this.status = status;
    }

    public static MensagemChatDTO fromEntity(Mensagem mensagem) {
        return new MensagemChatDTO(
                mensagem.getId(),
                mensagem.getConteudo(),
                mensagem.getDirecao(),
                mensagem.getDataHoraEnvio(),
                mensagem.getStatus()
        );
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getConteudo() {
        return conteudo;
    }

    public DirecaoType getDirecao() {
        return direcao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public StatusMensagem getStatus() {
        return status;
    }
}
