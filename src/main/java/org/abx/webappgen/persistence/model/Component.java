package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

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
    public Container container;

    @Column
    public boolean isContainer;

    @Column
    public String js;

}
