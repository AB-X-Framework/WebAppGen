package org.abx.webappgen.persistence.model;


import jakarta.persistence.*;

@Entity
@Table(name = "ArrayPairResource",
        indexes = {@Index(name = "ArrayPairResource_packageName", columnList = "packageName")})
public class ArrayPairResource {

    @Id
    public Long arrayPairResourceId;

    @Column(nullable = false)
    public String packageName;

    @Column(nullable = false)
    public String resourceName;

    @Column(nullable = false)
    public String owner;

    @Column(nullable = false)
    public String access;

}