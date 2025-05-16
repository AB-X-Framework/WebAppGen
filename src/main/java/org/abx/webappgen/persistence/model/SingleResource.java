package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "SingleResource")
public class SingleResource {
    @Id
    @Column(unique = true, nullable = false)
    public Long singleResourceId;

    @Column
    public String resourceValue;

    @OneToOne
    @JoinColumn(name = "resourceId", nullable = false)
    public Resource resource;
}
