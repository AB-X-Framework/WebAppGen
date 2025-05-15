package org.abx.webappgen.utils;


import jakarta.annotation.PostConstruct;
import org.abx.webappgen.controller.UserPageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PageImporter {
    @Autowired
    private UserPageController userPageController;

    @Value("${load.pages}")
    public boolean loadPages;

    @PostConstruct
    public void init() {
        System.out.println("PageImporter init!!!"+loadPages);
    }
}
