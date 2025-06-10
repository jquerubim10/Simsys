package br.com.savemed.model.auth.permission;

import br.com.savemed.model.auth.UserSave;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "USUARIO_PERFIL")
public class UsuarioPerfil {

    @EmbeddedId
    private UsuarioPerfilId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "ID_USUARIO")
    private UserSave usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("perfilId")
    @JoinColumn(name = "ID_PERFIL")
    private PerfilUsuario perfil;
}