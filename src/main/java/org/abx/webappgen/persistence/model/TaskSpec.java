package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TaskSpec")
public class TaskSpec {
    @Id
    @Column(unique = true, nullable = false)
    public Long taskSpecId;

    @Column(nullable = false)
    public String taskSpecName;

    @Column(nullable = false)
    public String packageName;

    @Column(nullable = false)
    public String description;
}
