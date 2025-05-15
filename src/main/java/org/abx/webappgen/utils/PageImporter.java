package org.abx.webappgen.utils;


import jakarta.annotation.PostConstruct;
import org.abx.util.StreamUtils;
import org.abx.webappgen.controller.UserPageController;
import org.abx.webappgen.creds.UserPageModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;

@Component
public class PageImporter {
    @Autowired
    private UserPageModel userPageModel;

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
            JSONArray pages = obj.getJSONArray("pages");
            for (int i = 0; i < pages.length(); i++) {
                JSONObject page = pages.getJSONObject(i);
                processPage(page);
            }
        }
    }

    private void processPage(JSONObject page){
        long id = userPageModel.createPageWithPagename(page.getString("name"),
                page.getString("title"));
    }
}
