package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ArrayEntry",indexes = {
        @Index(name = "ArrayEntry_arrayEntryId", columnList = "arrayEntryId"), // Index for better query performance
        @Index(name = "ArrayEntry_arrayResourceId", columnList = "arrayResourceId"), // Index for better query performance
})
public class ArrayEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long arrayEntryId;

    @Column(length = 2048, nullable = false)
    public String arrayValue;

    @Column
    public Long arrayResourceId;

}