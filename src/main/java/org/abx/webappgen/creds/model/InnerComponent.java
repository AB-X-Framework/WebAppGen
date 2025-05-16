package org.abx.webappgen.creds.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Collection;

@Entity
@Table(name = "InnerComponent" )
public class InnerComponent {
    @Id
    @Column(unique = true, nullable = false)
    public Long innerComponentId;


    @ManyToOne
    @JoinColumn(name = "componentId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Component parent;

    public long child;
}
