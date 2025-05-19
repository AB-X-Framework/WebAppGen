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

    @Column(nullable = false)
    public String resourceName;

    @Column(nullable = false)
    public String role;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    public String resourceValue;

}
