package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "Component" , indexes = {
        @Index(name = "idx_packageName", columnList = "packageName") // Index for better query performance
})
public class Component {

    @Id
    @Column(unique = true, nullable = false)
    public Long componentId;

    @Column(nullable = false)
    public String componentName;

    @Column(nullable = false)
    public String packageName;

    @Column(nullable = false)
    public boolean isContainer;

    @OneToOne(mappedBy = "component")
    public Container container;

    @OneToOne(mappedBy = "component")
    public Element element;

    @OneToMany
    public Collection<EnvValue> js;

}
