package br.com.savemed.model.dto.message;

import br.com.savemed.model.enums.*;
import br.com.savemed.model.message.Mensagem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
}
