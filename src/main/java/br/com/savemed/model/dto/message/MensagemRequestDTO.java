package br.com.savemed.model.dto.message;

import br.com.savemed.model.enums.CanalType;
import br.com.savemed.model.enums.TipoMensagem;
import br.com.savemed.model.message.EntityReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MensagemRequestDTO {

    @NotNull
    @Schema(description = "Canal de envio da mensagem", example = "WHATSAPP", requiredMode = Schema.RequiredMode.REQUIRED)
    private CanalType canal;

    @NotBlank
    @Schema(description = "Destinatário (número de telefone, email ou ID de sistema)", example = "+5514999999999", requiredMode = Schema.RequiredMode.REQUIRED)
    private String destinatario;

    @NotBlank
    @Schema(description = "Conteúdo da mensagem", example = "Olá, seu agendamento foi confirmado!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String conteudo;

    // Getters e Setters (ADICIONE OS FALTANTES!)
    @NotNull
    @Schema(description = "Tipo da mensagem", example = "LEMBRETE", requiredMode = Schema.RequiredMode.REQUIRED)
    private TipoMensagem tipoMensagem;

    @Schema(description = "Prioridade da mensagem (1 a 5)", example = "3", defaultValue = "3")
    private Integer prioridade = 3;

    @Schema(description = "ID externo para rastreamento", example = "MSG-12345")
    private String sistemaExternoId;

    @NotNull
    @Schema(description = "Referência à entidade relacionada", requiredMode = Schema.RequiredMode.REQUIRED)
    private EntityReference entidadeRef;
}