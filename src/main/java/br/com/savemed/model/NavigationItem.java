package br.com.savemed.model;

import br.com.savemed.model.builder.BuilderScreen;
import br.com.savemed.model.enums.Target;
import br.com.savemed.model.enums.Types;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "FUSE_NAVIGATION_ITEM")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"parent", "children", "screen"})  // Evita recursão infinita no toString()
public class NavigationItem implements Serializable {

    private static final long serialVersionUID = 1L;

    // Identificação
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, columnDefinition = "BIGINT")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "ID_NAME", nullable = false)
    private String idName;

    // Informações básicas
    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "SUBTITLE")
    private String subtitle;

    @Column(name = "TOOLTIP")
    private String tooltip;

    // Navegação
    @Column(name = "LINK")
    private String link;

    @Column(name = "FRAGMENT")
    private String fragment;

    @Column(name = "QUERY_PARAMS")
    private String queryParams;

    @Column(name = "QUERY_PARAMS_HANDLING")
    private String queryParamsHandling;

    // Metadados
    @Column(name = "META")
    private String meta;

    @Column(name = "ICON")
    private String icon;

    @Column(name = "FUNCTIONS")
    @JsonProperty("FUNCTION")
    private String function;

    // Tipos e classes
    @Column(name = "TARGET")
    @Enumerated(EnumType.STRING)
    private Target target;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private Types type;

    @Column(name = "CLASSES")
    private String classes;

    @Column(name = "BADGE")
    private String badge;

    // Flags booleanas
    @Column(name = "PRESERVE_FRAGMENT", columnDefinition = "boolean default false")
    private boolean preserveFragment;

    @Column(name = "COMPONENT_S", columnDefinition = "boolean default false")
    private boolean componentS;

    @Column(name = "EXTERNAL_LINK", columnDefinition = "boolean default false")
    private boolean externalLink;

    @Column(name = "EXACT_MATCH", columnDefinition = "boolean default false")
    private boolean exactMatch;

    @Column(name = "ID_PARENT", insertable = false, updatable = false)
    private Long idSidebarMenu;

    @Column(name = "IS_CHILDREN_SIDEBAR", columnDefinition = "boolean default false")
    private boolean isChildrenSidebar;

    @Column(name = "IS_ACTIVE_MATCH_OPTIONS", columnDefinition = "boolean default false")
    private boolean isActiveMatchOptions;

    @Column(name = "HIDDEN", columnDefinition = "boolean default false")
    private boolean hidden;

    @Column(name = "ACTIVE", columnDefinition = "boolean default false")
    private boolean active;

    @Column(name = "DISABLED", columnDefinition = "boolean default false")
    private boolean disabled;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PARENT")
    @JsonBackReference("navigation-parent")
    private NavigationItem parent;

    @OneToMany(mappedBy = "parent")
    @JsonManagedReference("navigation-parent")
    private List<NavigationItem> children;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCREEN_ID")
    @JsonIgnore
    private BuilderScreen screen;

    @PostPersist
    private void postPersist() {
        if (this.meta == null) {
            this.meta = String.valueOf(this.id);
        }
    }

    // Métodos auxiliares (opcionais)
    public boolean isRootItem() {
        return parent == null;
    }

    public void addChild(NavigationItem child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(NavigationItem child) {
        children.remove(child);
        child.setParent(null);
    }
}