package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Resource")
public class Resource {
    @Id
    @Column(unique = true, nullable = false)
    public Long resourceId;

    @Column
    public String resourceName;

    @Column
    public char resourceType;

    @OneToOne(mappedBy = "resource")
    public SingleResource resource;
}
