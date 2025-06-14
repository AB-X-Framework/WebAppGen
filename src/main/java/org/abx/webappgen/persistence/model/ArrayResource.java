package org.abx.webappgen.persistence.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "ArrayResource",
        indexes = {@Index(name = "ArrayResource_packageName", columnList = "packageName")})
public class ArrayResource {

    @Id
    public Long arrayResourceId;

    @Column(nullable = false)
    public String packageName;

    @Column(nullable = false)
    public String resourceName;


}