package org.abx.webappgen.creds.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "PageSection")
public class PageSection {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long pageSectionId;

    @ManyToOne
    @JoinColumn(name = "pageId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Page page;


    @ManyToOne
    @JoinColumn(name = "sectionId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Section section;
}
