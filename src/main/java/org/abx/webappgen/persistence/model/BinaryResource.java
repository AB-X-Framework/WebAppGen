package org.abx.webappgen.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Resource")
public class BinaryResource {
    @Id
    @Column(unique = true, nullable = false)
    public Long resourceId;

    @Column
    public String resourceName;

    @Column
    public String resourceType;

    @Column(columnDefinition = "MEDIUMBLOB")
    public byte[] resourceValue;

}
