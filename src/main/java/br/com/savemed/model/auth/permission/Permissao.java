package br.com.savemed.model.auth.permission;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PERMISSAO")
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PERFIL", nullable = false)
    private PerfilUsuario perfil;

    @Column(name = "ID_RECURSO", nullable = false)
    private Long idRecurso;

    @Column(name = "TIPO_RECURSO", nullable = false)
    private String tipoRecurso; // Ex: 'NAVIGATION_ITEM' ou 'BUILDER_SCREEN'

    @Column(name = "PODE_VISUALIZAR", nullable = false)
    private boolean podeVisualizar = false;

    @Column(name = "PODE_CRIAR", nullable = false)
    private boolean podeCriar = false;

    @Column(name = "PODE_EDITAR", nullable = false)
    private boolean podeEditar = false;

    @Column(name = "PODE_EXCLUIR", nullable = false)
    private boolean podeExcluir = false;

    @Column(name = "PODE_IMPRIMIR", nullable = false)
    private boolean podeImprimir = false;
}