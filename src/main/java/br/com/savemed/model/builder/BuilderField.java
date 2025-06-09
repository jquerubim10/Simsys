package br.com.savemed.model.builder;

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
@Table(name = "BUILDER_FIELD")
public class BuilderField implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placeholder;
    private String type;
    private String label;

    @Column(name = "FORM_CONTROL_NAME")
    private String formControlName;

    private Long lines;
    private String value;

    private String css;

    @Column(name = "CLASS_NAME")
    private String className;

    @Column(name = "COLUMN_NAME")
    private String columnName;
    private String mask;

    @Column(name = "INFO_SENSITIVE")
    private boolean infoSensitive;
    private boolean searchable;
    private boolean editable;
    private boolean visible;

    @Column(name = "VALIDATOR_REQUIRED")
    private boolean validatorRequired;
    @Column(name = "VALIDATOR_MIN")
    private Long validatorMin;
    @Column(name = "VALIDATOR_MAX")
    private Long validatorMax;
    @Column(name = "VALIDATOR_MESSAGE")
    private String validatorMessage;

    @Column(name = "FUNCTIONS_METHOD")
    private String functionMethod;

    @Column(name = "DIV_ID")
    private Long divId;
    @Column(name = "SCREEN_ID")
    private Long screenId;

    @Column(name = "SQL_TEXT")
    private String sqlText;

    @Column(name = "SQL_OBJ_TEXT")
    private String sqlObjText;

    @Column(name = "ORDER_ROW")
    private Long orderRow;

    @Column(name = "ORDER_SCREEN")
    private Long orderScreen;

    @Column(name = "NULL_VALUE", columnDefinition = "boolean default false")
    private boolean nullValue;

    @Column(name = "PREVIEW_ONLY", columnDefinition = "boolean default false")
    private boolean previewOnly;

    @Column(name = "DEFAULTS_VALUE", columnDefinition = "boolean default false")
    private boolean defaultsValue;

    @Column(name = "ACTIVE", columnDefinition = "boolean default true")
    private boolean active;
}
