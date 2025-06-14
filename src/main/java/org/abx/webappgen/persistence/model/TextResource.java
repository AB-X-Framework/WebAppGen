package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TextResource",
        indexes = {@Index(name = "TextResource_packageName", columnList = "packageName"),
                @Index(name = "TextResource_owner", columnList = "owner")})
public class TextResource {
    @Id
    @Column(unique = true, nullable = false)
    public Long textResourceId;

    @Column(nullable = false)
    public String packageName;

    @Column(nullable = false)
    public String resourceName;

    @Column(nullable = false)
    public String title;

    @Column(nullable = false)
    public long owner;

    @Column(nullable = false)
    public String role;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    public String resourceValue;

}
