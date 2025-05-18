package org.abx.webappgen.persistence.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "ArrayResource")
public class ArrayResource {

    @Id
    public Long arrayResourceId;

    // Other fields...

    @OneToMany(mappedBy = "arrayResource", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<ArrayEntry> resourceEntries;

    // Getters and Setters
}