package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MapEntry",indexes = {
        @Index(name = "MapEntry_mapEntryId", columnList = "mapEntryId"), // Index for better query performance
        @Index(name = "MapEntry_entryName", columnList = "entryName"), // Index for better query performance
})
public class MapEntry {

    @Id
    public Long mapEntryId;

    @Column(nullable = false)
    public String entryName;

    @Column(length = 2048, nullable = false)
    public String mapValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapResourceId")
    public MapResource mapResource;

}