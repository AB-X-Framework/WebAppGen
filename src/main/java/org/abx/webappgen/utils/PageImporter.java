package org.abx.webappgen.utils;


import jakarta.annotation.PostConstruct;
import org.abx.util.StreamUtils;
import org.abx.webappgen.controller.UserPageController;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;

@Component
public class PageImporter {
    @Autowired
    private UserPageController userPageController;

    @Value("${load.pages}")
    public boolean loadPages;


    @Value("${pages.resource}")
    public String pageSpecsPath;

    @PostConstruct
    public void init() throws Exception {
        System.out.println("PageImporter init!!!" + loadPages);
        if (loadPages) {

            String data = StreamUtils.readStream(new FileInputStream(pageSpecsPath));
            JSONObject obj = new JSONObject(data);
            System.out.println(obj.toString(1));
        }
    }
}
