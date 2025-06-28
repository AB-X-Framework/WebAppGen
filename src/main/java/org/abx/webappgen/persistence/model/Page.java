package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "Page", indexes = {
        @Index(name = "idx_pagename", columnList = "pageName") ,// Index for better query performance
        @Index(name = "idx_pagematchesId", columnList = "matchesId") ,// Index for better query performance
})
public class Page {

    @Id
    @Column(unique = true, nullable = false)
    public Long pageId;

    @Column( nullable = false)
    public String matches;

    @Column( nullable = false)
    public Long matchesId;

    @Column( nullable = false)
    public String pageName;

    @Column(nullable = false)
    public String packageName;

    @Column(nullable = false)
    public String role;

    @ManyToOne
    public Component component;
    @OneToMany
    @JoinTable(name = "PageTitle", joinColumns = @JoinColumn(name = "pageId"),
            inverseJoinColumns = @JoinColumn(name = "envValueId"))
    public Collection<EnvValue> pageTitle;

    @OneToMany
    @JoinTable(name = "PageCSS", joinColumns = @JoinColumn(name = "pageId"),
            inverseJoinColumns = @JoinColumn(name = "envValueId"))
    public Collection<EnvValue> css;

    @OneToMany
    @JoinTable(name = "PageScripts", joinColumns = @JoinColumn(name = "pageId"),
            inverseJoinColumns = @JoinColumn(name = "envValueId"))
    public Collection<EnvValue> scripts;


}
