package org.abx.webappgen.utils;


import jakarta.annotation.PostConstruct;
import org.abx.util.StreamUtils;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.ResourceModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;

@Component
public class PageImporter {
    @Autowired
    private PageModel pageModel;
    @Autowired
    private ResourceModel resourceModel;

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
            File resourceFile = new File(pageSpecsPath);
            String resourceFolder = resourceFile.getParent();
            String data = StreamUtils.readStream(new FileInputStream(resourceFile));
            JSONObject obj = new JSONObject(data);


            JSONObject resources = obj.getJSONObject("resources");
            processResource(resourceFolder, resources);


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
                page.getString("role"),
                page.getString("component"));

    }


    private void processBinaryComponent(String specsPath, JSONArray specs) throws Exception {
        for (int i = 0; i < specs.length(); i++) {
            JSONObject jsonResource = specs.getJSONObject(i);
            String name = jsonResource.getString("name");
            String file = specsPath + "/" + name;
            byte[] data = StreamUtils.readByteArrayStream(new FileInputStream(file));
            resourceModel.saveBinaryResource(name, jsonResource.getString("type"), data);
        }
    }

    private void processResource(String specsPath, JSONObject resource) throws Exception {
        processBinaryComponent(specsPath, resource.getJSONArray("binary"));
    }

    private void processComponents(JSONObject component) throws Exception {
        boolean isContainer = component.getBoolean("isContainer");
        String name = component.getString("name");
        if (isContainer) {
            pageModel.createContainer(name,
                    component.getJSONArray("js"),
                    component.getString("layout"),
                    component.getJSONArray("components"));
        } else {
            JSONArray specs = component.getJSONArray("specs");
            pageModel.createElement(name,
                    component.getJSONArray("js"),
                    component.getString("type"),
                    specs);
        }
    }


}
