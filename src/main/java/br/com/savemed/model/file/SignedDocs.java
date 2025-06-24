package br.com.savemed.model.file;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "SIGNED_DOCS")
public class SignedDocs implements Serializable {

    private static final long serialVersionUUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "SIGNED_USER_ID")
    private Long signedUserId;

    @NotBlank(message = "SOURCE_TABLE não pode ser vazio!")
    @Column(name = "SOURCE_TABLE")
    private String sourceTable;

    @Column(name = "SOURCE_TABLE_ID")
    private Long sourceTableId;

    @Column(name = "DOCUMENT_HASH")
    private String documentHash;

    @Column(name = "SIGNING_DATE")
    private String signingDate;

    @Column(name = "REVOCATION_DATE")
    private String revocationDate;

    @Column(name = "WHERE_CLAUSE_COLUMN")
    private String whereClauseColumn;

    @Column(name = "SIGN_TABLE_NAME")
    private String signTableName;

    @Column(name = "SIGN_COLUMN_NAME")
    private String signColumnName;

    @Lob
    @Column(name = "SIGNED_DOCUMENT")
    private byte[] signedDocument;

    @Lob
    @Column(name = "REVOCATION_DOCUMENT")
    private byte[] revocationDocument;

    public SignedDocs(String sourceTable, @NonNull Long signedUserId, Long sourceTableId, String documentHash, byte[] signedDocument, String whereClauseColumn, String signColumnName) {
        this.sourceTable = sourceTable;
        this.signedUserId = signedUserId;
        this.sourceTableId = sourceTableId;
        this.documentHash = documentHash;
        this.signedDocument = signedDocument;
        this.whereClauseColumn = whereClauseColumn;
        this.signColumnName = signColumnName;
    }

    public SignedDocs(String sourceTable, @NonNull Long signedUserId, Long sourceTableId, String documentHash, byte[] signedDocument, String whereClauseColumn) {
        this.sourceTable = sourceTable;
        this.signedUserId = signedUserId;
        this.sourceTableId = sourceTableId;
        this.documentHash = documentHash;
        this.signedDocument = signedDocument;
        this.whereClauseColumn = whereClauseColumn;
    }

    public SignedDocs(String sourceTable, @NonNull Long signedUserId, Long sourceTableId, String documentHash, byte[] signedDocument) {
        this.sourceTable = sourceTable;
        this.signedUserId = signedUserId;
        this.sourceTableId = sourceTableId;
        this.documentHash = documentHash;
        this.signedDocument = signedDocument;
    }

    @PrePersist
    private void updateSignedDate() {
        this.setSigningDate(new Date().toString());
    }
}
