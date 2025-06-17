package br.com.savemed.model.message;

import br.com.savemed.model.enums.EntidadeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
public class EntityReference implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(name = "EntidadeType")
    @Schema(description = "Tipo da entidade relacionada", example = "AGENDAMENTO", requiredMode = Schema.RequiredMode.REQUIRED)
    private EntidadeType type; // Mantenha o nome "type" para corresponder ao JSON

    @Column(name = "EntidadeID")
    @Schema(description = "ID da entidade relacionada", example = "90", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    // Construtor padrão
    public EntityReference() {}

    // Construtor parametrizado
    public EntityReference(EntidadeType type, Long id) {
        this.type = type;
        this.id = id;
    }

    // Getters e Setters
    public EntidadeType getType() {
        return type;
    }

    public void setType(EntidadeType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}