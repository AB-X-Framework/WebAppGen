package org.abx.webappgen.persistence.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "MapResource",
        indexes = {@Index(name = "MapResource_packageName", columnList = "packageName")}) // Index for better query performance)
public class MapResource {

    @Id
    public Long mapResourceId;

    @Column(nullable = false)
    public String resourceName;

    @Column(nullable = false)
    public String packageName;

    @OneToMany(mappedBy = "mapResource", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<MapEntry> resourceEntries;

    @Column(nullable = false)
    public String access;

    @Column(nullable = false)
    public String owner;
}