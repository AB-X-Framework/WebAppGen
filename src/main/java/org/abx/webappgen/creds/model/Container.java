package org.abx.webappgen.creds.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "ContainerComponent")
public class Container {
    @Id
    @Column(unique = true, nullable = false)
    public Long containerComponentId;

    @OneToOne
    @JoinColumn(name = "componentId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Component component;

    @ManyToOne
    @JoinColumn(name = "innerComponentId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public InnerComponent innerComponent;

    @Column
    public String layout;
}
