package org.abx.webappgen.creds.model;

import jakarta.persistence.*;

@Entity
@Table(name = "page" , indexes = {
        @Index(name = "idx_pagename", columnList = "pagename") // Index for better query performance
})
public class Page {

    @Id
    @Column(unique = true, nullable = false)
    public Long pageId;


    @Column(length = 60)
    public String pagename;


    @Column(length = 60)
    public String pageTitle;
}
