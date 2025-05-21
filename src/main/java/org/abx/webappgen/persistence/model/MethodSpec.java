package org.abx.webappgen.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "MethodSpec")
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
