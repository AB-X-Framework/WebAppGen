package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ArrayPairEntry", indexes = {
        @Index(name = "ArrayPairEntry_arrayPairEntryId", columnList = "arrayPairEntryId"), // Index for better query performance
        @Index(name = "ArrayPairEntry_arrayPairResourceId", columnList = "arrayPairResourceId"), // Index for better query performance
})
public class ArrayPairEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long arrayPairEntryId;

    @Column(length = 2048, nullable = false)
    public String arrayPairKey;

    @Column(length = 2048, nullable = false)
    public String arrayPairValue;

    @Column
    public Long arrayPairResourceId;

}