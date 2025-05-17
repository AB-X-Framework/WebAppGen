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

            JSONArray components = obj.getJSONArray("components");
            for (int i = 0; i < components.length(); i++) {
                JSONObject page = components.getJSONObject(i);
                processComponents(resourceFolder, page);
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


    private void processComponents(String scriptPath, JSONObject component) throws Exception {
        boolean isContainer = component.getBoolean("isContainer");
        String name = component.getString("name");
        if (isContainer) {
            pageModel.createContainer(name,
                    component.getJSONArray("js"),
                    component.getString("layout"),
                    component.getJSONArray("components"));
        } else {
            JSONArray specs = component.getJSONArray("specs");
            String type = component.getString("type");
            if ("img".equals(type)) {
                processImgComponent(scriptPath, specs);
            }
            pageModel.createElement(name,
                    component.getJSONArray("js"),
                    component.getString("type"),
                    specs);
        }
    }

    private void processImgComponent(String scriptPath, JSONArray specs) throws Exception {
        for (int i = 0; i < specs.length(); i++) {
            JSONObject jsonComponent = specs.getJSONObject(i).getJSONObject("value");
            String src = jsonComponent.getString("src");
            String file = scriptPath+"/"+src;
            byte[] data = StreamUtils.readByteArrayStream(new FileInputStream(file));
            resourceModel.saveBinaryResource(src, detectMimeType(data), data);
        }
    }

    public static String detectMimeType(byte[] bytes) {
        if (bytes == null || bytes.length < 8) {
            return "application/octet-stream";
        }

        // Check PNG
        if (bytes[0] == (byte) 0x89 && bytes[1] == (byte) 0x50 &&
                bytes[2] == (byte) 0x4E && bytes[3] == (byte) 0x47 &&
                bytes[4] == (byte) 0x0D && bytes[5] == (byte) 0x0A &&
                bytes[6] == (byte) 0x1A && bytes[7] == (byte) 0x0A) {
            return "image/png";
        }

        // Check JPEG
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8) {
            return "image/jpeg";
        }

        return "application/octet-stream";
    }
}
