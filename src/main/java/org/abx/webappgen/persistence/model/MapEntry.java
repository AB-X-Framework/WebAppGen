package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MapEntry")
public class MapEntry {

    @Id
    public Long mapEntryId;
    @Column
    public String entryName;
    // Other fields...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapResourceId")
    public MapResource mapResource;

    @Column(length = 2048)
    public String value;
    // Getters and Setters
}