package org.abx.webappgen.creds.model;

import jakarta.persistence.*;

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


}
