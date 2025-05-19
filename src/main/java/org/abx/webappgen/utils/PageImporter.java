package org.abx.webappgen.utils;


import jakarta.annotation.PostConstruct;
import org.abx.util.StreamUtils;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.ResourceModel;
import org.abx.webappgen.persistence.dao.MethodSpecRepository;
import org.abx.webappgen.persistence.model.MethodSpec;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class PageImporter {
    @Autowired
    private PageModel pageModel;
    @Autowired
    private ResourceModel resourceModel;
    @Autowired
    private MethodSpecRepository methodSpecRepository;

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
            String specsFolder = resourceFile.getParent();
            String data = StreamUtils.readStream(new FileInputStream(resourceFile));
            JSONObject obj = new JSONObject(data);

            JSONArray methods = obj.getJSONArray("methods");
            for (int i = 0; i < methods.length(); i++) {
                JSONObject method = methods.getJSONObject(i);
                processMethods(specsFolder, method);
            }

            JSONObject resources = obj.getJSONObject("resources");
            processResource(specsFolder, resources);


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

    private void processMethods(String specsFolder, JSONObject method) throws IOException {
        MethodSpec specs = new MethodSpec();
        specs.methodName = method.getString("name");
        specs.description = method.getString("description");
        specs.methodSpecId = PageModel.elementHashCode(specs.methodName);
        specs.methodJS = StreamUtils.readStream(new FileInputStream(
                specsFolder + "/methods/" + specs.methodName + ".js" ));
        specs.type = method.getString("type");
        specs.outputName = method.getString("outputName");
        specs.role = method.getString("role");
        methodSpecRepository.save(specs);
    }

    private void processBinaryResource(String specsPath, JSONArray specs) throws Exception {
        for (int i = 0; i < specs.length(); i++) {
            JSONObject jsonResource = specs.getJSONObject(i);
            String name = jsonResource.getString("name");
            String file = specsPath + "/resources/" + name;
            byte[] data = StreamUtils.readByteArrayStream(new FileInputStream(file));
            resourceModel.saveBinaryResource(name, jsonResource.getString("contentType"), data);
        }
    }

    private void processResource(String specsPath, JSONObject resource) throws Exception {
        processBinaryResource(specsPath, resource.getJSONArray("binary"));
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
