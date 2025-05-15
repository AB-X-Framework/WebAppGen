package org.abx.webappgen.creds.model;

import jakarta.persistence.*;

@Entity
@Table(name = "page")
public class Page {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(length = 60)
    private String pagename;


    @Column(length = 60)
    private String tile;
}
