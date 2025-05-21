package org.abx.webappgen.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "BinaryResource")
public class BinaryResource {
    @Id
    @Column(unique = true, nullable = false)
    public Long binaryResourceId;

    @Column(nullable = false)
    public String resourceName;

    @Column(nullable = false)
    public String packageName;

    @Column(nullable = false)
    public String contentType;

    @Column(nullable = false)
    public String role;

    @Column(columnDefinition = "MEDIUMBLOB",nullable = false)
    public byte[] resourceValue;

}
