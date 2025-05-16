package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Collection;

@Entity
@Table(name = "Element")
public class Element {
    @Id
    @Column(unique = true, nullable = false)
    public Long elementId;

    @OneToOne
    @JoinColumn(name = "componentId", nullable = false)
    public Component component;

    @Column
    public String type;

    @OneToMany
    public Collection<EnvValue> specs;

}
