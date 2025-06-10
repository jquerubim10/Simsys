package br.com.savemed.model.auth.permission;

import br.com.savemed.model.auth.UserSave;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PERMISSAO_ADICIONAL_USUARIO")
public class PermissaoAdicionalUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private UserSave usuario;

    @Column(name = "ID_RECURSO", nullable = false)
    private Long idRecurso;

    @Column(name = "TIPO_RECURSO", nullable = false)
    private String tipoRecurso;

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