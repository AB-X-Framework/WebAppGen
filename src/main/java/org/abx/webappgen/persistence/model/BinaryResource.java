package org.abx.webappgen.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "BinaryResource")
public class BinaryResource {
    @Id
    @Column(unique = true, nullable = false)
    public Long binaryResourceId;

    @Column
    public String resourceName;

    @Column
    public String contentType;

    @Column
    public String role;

    @Column(columnDefinition = "MEDIUMBLOB")
    public byte[] resourceValue;

}
