package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "EnvValue")
public class EnvValue {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long envValueId;

    @Column(nullable = false)
    public String env;

    @Column(nullable = false)
    public String value;
}
