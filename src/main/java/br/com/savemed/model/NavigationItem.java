package br.com.savemed.model;

import br.com.savemed.model.enums.Target;
import br.com.savemed.model.enums.Types;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "FUSE_NAVIGATION_ITEM")
@NoArgsConstructor
@EqualsAndHashCode
public class NavigationItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ID_NAME", nullable = false)
    private String idName;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "SUBTITLE")
    private String subtitle;

    @Column(name = "TOOLTIP")
    private String tooltip;

    @Column(name = "LINK")
    private String link;

    @Column(name = "FRAGMENT")
    private String fragment;

    @Column(name = "QUERY_PARAMS")
    private String queryParams;

    @Column(name = "QUERY_PARAMS_HANDLING")
    private String queryParamsHandling;

    @Column(name = "META")
    private String meta;

    @Column(name = "ICON")
    private String icon;

    @Column(name = "FUNCTIONS")
    @JsonProperty(namespace = "FUNCTION")
    private String function;

    @Column(name = "TARGET")
    @Enumerated(EnumType.STRING)
    private Target target;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private Types type;

    @Column(name = "SCREEN_ID")
    private String screenId;

    @Column(name = "CLASSES")
    private String classes;

    @Column(name = "BADGE")
    private String badge;

    @Column(name = "PRESERVE_FRAGMENT", columnDefinition = "boolean default false")
    private boolean preserveFragment;

    @Column(name = "COMPONENT_S", columnDefinition = "boolean default false")
    private boolean componentS;

    @Column(name = "EXTERNAL_LINK", columnDefinition = "boolean default false")
    private boolean externalLink;

    @Column(name = "EXACT_MATCH", columnDefinition = "boolean default false")
    private boolean exactMatch;

    @Column(name = "IS_CHILDREN_SIDEBAR", columnDefinition = "boolean default false")
    private boolean isChildrenSidebar;

    @Column(name = "ID_SIDEBAR_MENU")
    private Long idSidebarMenu;

    @Column(name = "IS_ACTIVE_MATCH_OPTIONS", columnDefinition = "boolean default false")
    private boolean isActiveMatchOptions;

    @Column(name = "HIDDEN", columnDefinition = "boolean default false")
    private boolean hidden;

    @Column(name = "ACTIVE", columnDefinition = "boolean default false")
    private boolean active;

    @Column(name = "DISABLED", columnDefinition = "boolean default false")
    private boolean disabled;

    @PostPersist
    private void setMeta() {
        this.setMeta(this.getId().toString());
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PARENT")
    private NavigationItem parent; // O campo para o qual o setParent() será gerado

    @OneToMany(mappedBy = "parent")
    private List<NavigationItem> children;
}
