package org.abx.webappgen.persistence.model;
import jakarta.persistence.*;

@Entity
public class ArrayEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long arrayEntryId;

    // Other fields...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrayResourceId")
    public ArrayResource arrayResource;

    @Column(length = 2048)
    public String value;
    // Getters and Setters
}