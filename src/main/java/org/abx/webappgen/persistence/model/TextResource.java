package org.abx.webappgen.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TextResource")
public class TextResource {
    @Id
    @Column(unique = true, nullable = false)
    public Long textResourceId;

    @Column
    public String resourceName;
    @Column
    public String role;

    @Column(columnDefinition = "MEDIUMTEXT")
    public String resourceValue;

}
