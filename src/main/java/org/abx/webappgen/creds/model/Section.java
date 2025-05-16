package org.abx.webappgen.creds.model;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "Section" , indexes = {
        @Index(name = "idx_sectionname", columnList = "sectionName") // Index for better query performance
})
public class Section {

    @Id
    @Column(unique = true, nullable = false)
    public Long sectionId;


    @Column(length = 60)
    public String sectionName;


    @OneToMany(mappedBy = "section")
    private Collection<PageSections> pageSections;
}
