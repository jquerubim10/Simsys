package br.com.savemed.model.builder;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "BUILDER_SCREEN")
public class BuilderScreen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "GROUP_ID")
    private Long groupId;

    private String title;
    private String icon;

    @Column(name = "DATE_DISABLED")
    private String dateDisabled;

    @Column(name = "DATE_CREATED")
    private String dateCreated;

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

    @Column(name = "PREVIEW_OBJECT_TXT")
    private String previewObjectText;

    @Column(name = "PRE_SAVE_TXT")
    private String preSaveListTxt;

    @Column(name = "PRE_SAVE_FUNCTION_TXT")
    private String preSaveFunctionListTxt;

    @Column(name = "DIV_JSON")
    private String divJson;

    @Column(name = "PRO_SAVE_TXT")
    private String proSaveListTxt;

    @Column(name = "WHERE_CLAUSE")
    private String whereClause;

    @Column(name = "TYPE_TAB")
    private String typeTab;

    @Column(name = "EXECUTE_OPTION")
    private boolean executeOption;

    @Column(name = "ACTIVE", columnDefinition = "boolean default false")
    private boolean active;

    @Column(name = "COMPONENT_S", columnDefinition = "boolean default false")
    private boolean componentS;

    @Column(name = "HISTORY_S", columnDefinition = "boolean default false")
    private boolean historyC;

    @Column(name = "ID_SIDEBAR_MENU")
    private Long idSidebarMenu;

    @Column(name = "CHILDREN_SCREEN", columnDefinition = "boolean default false")
    private boolean childrenScreen;

    @Column(name = "LIST_C", columnDefinition = "boolean default false")
    private boolean listC;

    @PrePersist
    private void updateDate() {
        this.setDateCreated(new Date().toString());
    }
}
