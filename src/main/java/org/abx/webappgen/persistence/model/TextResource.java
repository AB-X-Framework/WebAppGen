package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Resource")
public class TextResource {
    @Id
    @Column(unique = true, nullable = false)
    public Long resourceId;

    @Column
    public String resourceName;

    @Column(columnDefinition = "MEDIUMTEXT")
    public String resourceValue;

}
