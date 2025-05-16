package org.abx.webappgen.creds.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "PageSections")
public class PageSections {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pageSectionId;

    @ManyToOne
    @JoinColumn(name = "pageId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Page page;


    @ManyToOne
    @JoinColumn(name = "sectionId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Section section;
}
