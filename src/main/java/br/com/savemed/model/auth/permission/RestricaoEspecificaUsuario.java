package br.com.savemed.model.auth.permission;

import br.com.savemed.model.auth.UserSave;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "RESTRICAO_ESPECIFICA_USUARIO")
public class RestricaoEspecificaUsuario {

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

    @Column(name = "NEGAR_VISUALIZAR", nullable = false)
    private boolean negarVisualizar = false;

    @Column(name = "NEGAR_CRIAR", nullable = false)
    private boolean negarCriar = false;

    @Column(name = "NEGAR_EDITAR", nullable = false)
    private boolean negarEditar = false;

    @Column(name = "NEGAR_EXCLUIR", nullable = false)
    private boolean negarExcluir = false;

    @Column(name = "NEGAR_IMPRIMIR", nullable = false)
    private boolean negarImprimir = false;
}