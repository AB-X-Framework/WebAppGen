package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "BinaryResource",
        indexes = {@Index(name = "BinaryResource_packageName", columnList = "packageName"),
                @Index(name = "BinaryResource_contentType", columnList = "contentType")})
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
    public String owner;

    @Column(nullable = false)
    public String access;


    @Column(nullable = false)
    public long hashcode;

    @Column(columnDefinition = "MEDIUMBLOB", nullable = false)
    public byte[] resourceValue;

}
