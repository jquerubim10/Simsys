package br.com.savemed.model.dto.message;

import br.com.savemed.model.enums.CanalType;
import br.com.savemed.model.enums.TipoMensagem;
import br.com.savemed.model.message.EntityReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    // Getters e Setters (ADICIONE OS FALTANTES!)
    public TipoMensagem getTipoMensagem() {
        return tipoMensagem;
    }

    public void setTipoMensagem(TipoMensagem tipoMensagem) {
        this.tipoMensagem = tipoMensagem;
    }

    public CanalType getCanal() {
        return canal;
    }

    public void setCanal(CanalType canal) {
        this.canal = canal;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    public String getSistemaExternoId() {
        return sistemaExternoId;
    }

    public void setSistemaExternoId(String sistemaExternoId) {
        this.sistemaExternoId = sistemaExternoId;
    }

    public EntityReference getEntidadeRef() {
        return entidadeRef;
    }

    public void setEntidadeRef(EntityReference entidadeRef) {
        this.entidadeRef = entidadeRef;
    }
}