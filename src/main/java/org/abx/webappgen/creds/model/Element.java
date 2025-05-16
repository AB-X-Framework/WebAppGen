package org.abx.webappgen.creds.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "SpecsComponent")
public class Element {
    @Id
    @Column(unique = true, nullable = false)
    public Long elementId;

    @OneToOne
    @JoinColumn(name = "componentId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Component component;

    @Column
    public String type;

    @Column
    public String specs;

    @Column
    public String js;
}
