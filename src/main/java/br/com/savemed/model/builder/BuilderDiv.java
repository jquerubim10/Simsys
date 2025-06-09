package br.com.savemed.model.builder;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "BUILDER_DIV")
public class BuilderDiv implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "FIELD_JSON")
    private String fieldJson;

    @Column(name = "SCREEN_ID")
    private Long screenId;

    @Column(name = "ACTIVE", columnDefinition = "boolean default true")
    private boolean active;
}
