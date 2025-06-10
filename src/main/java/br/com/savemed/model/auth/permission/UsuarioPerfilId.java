package br.com.savemed.model.auth.permission;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsuarioPerfilId implements Serializable {

    @Column(name = "ID_USUARIO")
    private Integer usuarioId;

    @Column(name = "ID_PERFIL")
    private Integer perfilId;
}