package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Collection;

@Entity
@Table(name = "Container")
public class Container {
    @Id
    @Column(unique = true, nullable = false)
    public Long containerId;

    @OneToOne
    @JoinColumn(name = "componentId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Component component;

    @OneToMany
    @JoinColumn(name = "container", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Collection<InnerComponent> innerComponent;

    @Column
    public String layout;
}
