package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "BinaryResource",
        indexes = {@Index(name = "BinaryResource_packageName", columnList = "packageName")})
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
