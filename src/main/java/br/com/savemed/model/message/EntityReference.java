package br.com.savemed.model.message;

import br.com.savemed.model.enums.EntidadeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EntityReference implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(name = "EntidadeType")
    @Schema(description = "Tipo da entidade relacionada", example = "AGENDAMENTO", requiredMode = Schema.RequiredMode.REQUIRED)
    private EntidadeType type; // Mantenha o nome "type" para corresponder ao JSON

    @Column(name = "EntidadeID")
    @Schema(description = "ID da entidade relacionada", example = "90", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
}