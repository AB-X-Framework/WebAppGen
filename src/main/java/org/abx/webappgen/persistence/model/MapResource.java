package org.abx.webappgen.persistence.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "MapResource")
public class MapResource {

    @Id
    public Long mapResourceId;
    @Column
    public String resourceName;
    // Other fields...

    @OneToMany(mappedBy = "mapResource", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<MapEntry> resourceEntries;

    // Getters and Setters
}