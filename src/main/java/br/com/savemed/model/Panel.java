package br.com.savemed.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "PANEL")
@NoArgsConstructor
@EqualsAndHashCode
public class Panel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ID_NAME")
    private String idName;

    private String title;
    private String description;
    private String icon;
    private String url;

    @Column(name = "ACTIVE", columnDefinition = "boolean default true")
    private boolean active;
}
