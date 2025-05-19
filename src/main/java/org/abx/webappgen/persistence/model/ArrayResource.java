package org.abx.webappgen.persistence.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "ArrayResource")
public class ArrayResource {

    @Id
    public Long arrayResourceId;


    @Column(nullable = false)
    public String resourceName;

    @OneToMany(mappedBy = "arrayResource", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<ArrayEntry> resourceEntries;

}