package org.abx.webappgen.utils;


import jakarta.annotation.PostConstruct;
import org.abx.util.StreamUtils;
import org.abx.webappgen.persistence.PageModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;

@Component
public class PageImporter {
    @Autowired
    private PageModel pageModel;

    @Value("${load.pages}")
    public boolean loadPages;

    @Value("${drop.pages}")
    public boolean dropPages;

    @Value("${pages.resource}")
    public String pageSpecsPath;

    @PostConstruct
    public void init() throws Exception {
        if (dropPages) {
            pageModel.clean();
        }
        if (loadPages) {

            String data = StreamUtils.readStream(new FileInputStream(pageSpecsPath));
            JSONObject obj = new JSONObject(data);

            JSONArray components = obj.getJSONArray("components");
            for (int i = 0; i < components.length(); i++) {
                JSONObject page = components.getJSONObject(i);
                processComponents(page);
            }

            JSONArray pages = obj.getJSONArray("pages");
            for (int i = 0; i < pages.length(); i++) {
                JSONObject page = pages.getJSONObject(i);
                processPage(page);
            }

        }
    }

    private void processPage(JSONObject page) {
        long id = pageModel.createPageWithPageName(
                page.getString("name"),
                page.getString("title"),
                page.getString("component"));

    }


    private void processComponents(JSONObject component) {
        boolean isContainer = component.getBoolean("isContainer");
        String name = component.getString("name");
        if (isContainer){
            pageModel.createContainer(name,
                    component.getJSONArray("js"),
                    component.getString("layout"),
                    component.getJSONArray("components"));
        }else {
            pageModel.createElement(name,
                    component.getJSONArray("js"),
                    component.getString("type"),
                    component.get("specs").toString());
        }
    }
}
