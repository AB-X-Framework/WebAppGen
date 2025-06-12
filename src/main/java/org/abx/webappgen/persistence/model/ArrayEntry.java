package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ArrayEntry",indexes = {
        @Index(name = "ArrayEntry_arrayEntryId", columnList = "arrayEntryId") // Index for better query performance
})
public class ArrayEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long arrayEntryId;

    @Column(length = 2048, nullable = false)
    public String arrayValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrayResourceId", nullable = false)
    public ArrayResource arrayResource;

}