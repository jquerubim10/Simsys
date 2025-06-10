package br.com.savemed.model.auth.permission;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set; // Importe a classe Set

@Entity
@Getter
@Setter
@Table(name = "PERFILUSUARIO")
public class PerfilUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERFILUSUARIO")
    private Integer id;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO")
    private String descricao;

    @Column(name = "ATUALIZACAO", nullable = false)
    private String atualizacao;

    // --- ADICIONE ESTA SEÇÃO ---
    // Mapeamento para o outro lado do relacionamento com UsuarioPerfil.
    // 'mappedBy = "perfil"' diz ao JPA: "A gestão deste relacionamento
    // é feita pelo campo 'perfil' na classe UsuarioPerfil".
    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UsuarioPerfil> usuarios;
    // -------------------------

}