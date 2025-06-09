package br.com.savemed.model.file;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Table(name = "PFX_USER")
public class PfxUser implements Serializable {

    private static final long serialVersionUUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "TITLE")
    private String title;

    @Lob
    @Column(name = "DATA_BLOB")
    private byte[] data;

    public PfxUser(String username, String title, byte[] data) {
        this.username = username;
        this.title = title;
        this.data = data;
    }
}
