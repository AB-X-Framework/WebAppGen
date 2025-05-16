package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TextResource")
public class TextResource {
    @Id
    @Column(unique = true, nullable = false)
    public Long textResourceId;

    @Column
    public String resourceName;

    @Column(columnDefinition = "MEDIUMTEXT")
    public String resourceValue;

}
