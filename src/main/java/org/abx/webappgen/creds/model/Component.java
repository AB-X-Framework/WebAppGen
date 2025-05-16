package org.abx.webappgen.creds.model;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "Component" , indexes = {
        @Index(name = "idx_componentName", columnList = "componentName") // Index for better query performance
})
public class Component {

    @Id
    @Column(unique = true, nullable = false)
    public Long componentId;


    @Column(length = 60)
    public String componentName;


    @OneToOne(mappedBy = "component")
    public ContainerComponent containerComponent;

    @Column
    public boolean container;


}
