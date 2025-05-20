package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "Page", indexes = {
        @Index(name = "idx_pagename", columnList = "pageName") // Index for better query performance
})
public class Page {

    @Id
    @Column(unique = true, nullable = false)
    public Long pageId;

    @Column(length = 60, nullable = false)
    public String pageName;

    @Column(length = 60, nullable = false)
    public String pageTitle;

    @Column(length = 60, nullable = false)
    public String role;

    @ManyToOne
    public Component component;

    @OneToMany
    @JoinTable(name = "PageCSS", joinColumns = @JoinColumn(name = "pageId"),
            inverseJoinColumns = @JoinColumn(name = "envValueId"))
    public Collection<EnvValue> css;

    @OneToMany
    @JoinTable(name = "PageScripts", joinColumns = @JoinColumn(name = "pageId"),
            inverseJoinColumns = @JoinColumn(name = "envValueId"))
    public Collection<EnvValue> scripts;


}
