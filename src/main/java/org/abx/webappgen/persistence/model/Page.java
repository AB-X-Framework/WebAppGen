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


    @Column(length = 60)
    public String pageName;


    @Column(length = 60)
    public String pageTitle;

    @Column
    public boolean header;

    @Column
    public boolean footer;

    @OneToMany(mappedBy = "page")
    public Collection<PageComponent> pageComponents;


}
