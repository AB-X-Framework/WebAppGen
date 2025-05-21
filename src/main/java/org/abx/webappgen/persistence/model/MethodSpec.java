package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MethodSpec",
        indexes = {@Index(name = "idx_packageName", columnList = "packageName")})
public class MethodSpec {
    @Id
    @Column(unique = true, nullable = false)
    public Long methodSpecId;

    @Column(nullable = false)
    public String methodName;

    @Column(nullable = false)
    public String packageName;

    @Column(nullable = false)
    public String type;

    @Column(nullable = false)
    public String outputName;

    @Column(length = 11,nullable = false)
    public String role;

    @Column(nullable = false)
    public String description;

    @Column(columnDefinition = "MEDIUMTEXT",nullable = false)
    public String methodJS;

}
