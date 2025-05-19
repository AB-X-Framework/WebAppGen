package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "InnerComponent" )
public class InnerComponent {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long innerComponentId;


    @Column(nullable = false)
    public String innerId;

    @Column(nullable = false)
    public String size;

    @Column(nullable = false)
    public String env;

    @ManyToOne
    @JoinColumn(name = "containerId", nullable = false)
    public Container parent;

    @ManyToOne
    @JoinColumn(name = "componentId", nullable = false)
    public Component child;

}
