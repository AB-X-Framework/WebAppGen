package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "PageComponent")
public class PageComponent {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long pageComponentId;

    @ManyToOne
    @JoinColumn(name = "pageId", nullable = false)
    public Page page;


    @ManyToOne
    @JoinColumn(name = "componentId", nullable = false)
    public Component component;
}
