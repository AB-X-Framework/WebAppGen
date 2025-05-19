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

    @Column
    public String methodName;

    @Column
    public String methodDescription;

    @Column(columnDefinition = "MEDIUMTEXT")
    public String methodJS;

}
