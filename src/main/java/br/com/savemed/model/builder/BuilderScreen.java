package br.com.savemed.model.builder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "BUILDER_SCREEN")
public class BuilderScreen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- RELACIONAMENTO CORRIGIDO ---
    // O campo groupId foi substituído por um relacionamento de objeto.
    // O JPA cuidará da coluna GROUP_ID no banco.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    private MenuGroup menuGroup;

    private String title;
    private String icon;

    // --- DATAS CORRIGIDAS ---
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_DISABLED")
    private Date dateDisabled;

    // A anotação @CreationTimestamp cuida de preencher a data automaticamente
    // na inserção, substituindo a necessidade do método @PrePersist.
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", updatable = false)
    private Date dateCreated;


    // --- CAMPO PROBLEMÁTICO REMOVIDO ---
    // O campo idSidebarMenu foi removido para quebrar o ciclo de dependência com NavigationItem.
    // A responsabilidade de saber qual tela um item de menu abre é do NavigationItem.


    // --- OUTROS CAMPOS MANTIDOS ---
    @Column(name = "TABLE_NAME")
    private String tableName;

    @Column(name = "ID_SCREEN_C")
    private Long idScreenC;

    @Column(name = "SCREEN_SING")
    private boolean screenSign;

    @Column(name = "SCREEN_PREVIEW")
    private boolean screenPreview;

    @Column(name = "SCREEN_LOGIN")
    private boolean screenLogin;

    @Lob // Para textos longos, é bom usar @Lob
    @Column(name = "PREVIEW_OBJECT_TXT")
    private String previewObjectText;

    @Lob
    @Column(name = "PRE_SAVE_TXT")
    private String preSaveListTxt;

    @Lob
    @Column(name = "PRE_SAVE_FUNCTION_TXT")
    private String preSaveFunctionListTxt;

    @Lob
    @Column(name = "DIV_JSON")
    private String divJson;

    @Lob
    @Column(name = "PRO_SAVE_TXT")
    private String proSaveListTxt;

    @Lob
    @Column(name = "WHERE_CLAUSE")
    private String whereClause;

    @Column(name = "TYPE_TAB")
    private String typeTab;

    @Column(name = "EXECUTE_OPTION")
    private boolean executeOption;

    @Column(name = "ACTIVE")
    private boolean active = false;

    @Column(name = "COMPONENT_S")
    private boolean componentS = false;

    @Column(name = "HISTORY_S")
    private boolean historyC = false;

    @Column(name = "CHILDREN_SCREEN")
    private boolean childrenScreen = false;

    @Column(name = "LIST_C")
    private boolean listC = false;

    @PrePersist
    private void updateDate() {
        // Atribui um novo objeto Date ao campo dateCreated, que também é do tipo Date.
        this.setDateCreated(new Date());
    }

}