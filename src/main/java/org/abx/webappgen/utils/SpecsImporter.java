package org.abx.webappgen.utils;


import jakarta.annotation.PostConstruct;
import org.abx.util.StreamUtils;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.ResourceModel;
import org.abx.webappgen.persistence.dao.MethodSpecRepository;
import org.abx.webappgen.persistence.dao.UserRepository;
import org.abx.webappgen.persistence.model.MethodSpec;
import org.abx.webappgen.persistence.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class SpecsImporter {
    @Autowired
    private PageModel pageModel;
    @Autowired
    private ResourceModel resourceModel;
    @Autowired
    private MethodSpecRepository methodSpecRepository;

    @Value("${load.app}")
    public boolean loadApp;

    @Value("${drop.app}")
    public boolean dropApp;

    @Value("${app.specs}")
    public String appSpecsPath;
    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() throws Exception {
        if (dropApp) {
            pageModel.clean();
        }
        if (loadApp) {
            uploadSpecs(appSpecsPath);
        }
    }

    public boolean uploadBinarySpecs(byte[] zipFolder) throws Exception {
        String path = ZipUtils.unzipToTempFolder(zipFolder);
        uploadSpecs(path);
        return true;
    }

    public void uploadSpecs(String specsFolder) throws Exception {
        File resourceFile = new File(specsFolder + "/specs.json");
        String data = StreamUtils.readStream(new FileInputStream(resourceFile));
        JSONObject obj = new JSONObject(data);

        processUsers(specsFolder, obj.getString("users"));
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
            processComponents(page, false);
        }
        ;
        for (int i = 0; i < components.length(); i++) {
            JSONObject page = components.getJSONObject(i);
            processComponents(page, true);
        }

        JSONArray pages = obj.getJSONArray("pages");
        for (int i = 0; i < pages.length(); i++) {
            JSONObject page = pages.getJSONObject(i);
            processPage(page);
        }

    }

    private void processUsers(String specsFolder, String userfile) throws IOException {
        JSONArray users = new JSONArray(StreamUtils.readStream(new FileInputStream(
                specsFolder + "/" + userfile)));
        for (int i = 0; i < users.length(); i++) {
            JSONObject jsonUser = users.getJSONObject(i);
            User user = new User();
            user.username = jsonUser.getString("username");
            user.userId = PageModel.elementHashCode(user.username);
            user.password = jsonUser.getString("password");
            user.enabled = jsonUser.getBoolean("enabled");
            user.role = jsonUser.getString("role");
            userRepository.save(user);
        }
    }

    private void processPage(JSONObject page) {
        long id = pageModel.createPageWithPageName(
                page.getString("name"),
                page.getString("package"),
                page.getString("title"),
                page.getString("role"),
                page.getString("component"),
                page.getJSONArray("css"),
                page.getJSONArray("scripts"));

    }

    private void processMethods(String specsFolder, JSONObject method) throws IOException {
        MethodSpec specs = new MethodSpec();
        specs.methodName = method.getString("name");
        specs.packageName = method.getString("package");
        specs.description = method.getString("description");
        specs.methodSpecId = PageModel.elementHashCode(specs.methodName);
        specs.methodJS = StreamUtils.readStream(new FileInputStream(
                specsFolder + "/methods/" + specs.methodName + ".js"));
        specs.type = method.getString("type");
        specs.outputName = method.getString("outputName");
        specs.role = method.getString("role");
        methodSpecRepository.save(specs);
    }

    private void processArrayResource(String specsPath, JSONArray arrayResources) throws Exception {
        for (int i = 0; i < arrayResources.length(); i++) {
            JSONObject arrayResource = arrayResources.getJSONObject(i);
            String arrayName = arrayResource.getString("name");
            String arrayData = StreamUtils.readStream(new FileInputStream(specsPath + "/array/" + arrayName + ".json"));
            resourceModel.saveArrayResource(arrayName, arrayResource.getString("package"), new JSONArray(arrayData));
        }
    }

    private void processMapResource(String specsPath, JSONArray mapResources) throws Exception {
        for (int i = 0; i < mapResources.length(); i++) {
            JSONObject mapResource = mapResources.getJSONObject(i);
            String mapName = mapResource.getString("name");
            String arrayData = StreamUtils.readStream(new FileInputStream(specsPath + "/map/" + mapName + ".json"));
            resourceModel.saveMapResource(mapName, mapResource.getString("name"), new JSONObject(arrayData));
        }
    }

    private void processBinaryResource(String specsPath, JSONArray specs) throws Exception {
        for (int i = 0; i < specs.length(); i++) {
            JSONObject jsonResource = specs.getJSONObject(i);
            String name = jsonResource.getString("name");
            String file = specsPath + "/binary/" + name;
            byte[] data = StreamUtils.readByteArrayStream(new FileInputStream(file));
            resourceModel.saveBinaryResource(name,   jsonResource.getString("package"),
                    jsonResource.getString("contentType"),data, jsonResource.getString("role"));
        }
    }

    private void processTextResource(String specsPath, JSONArray specs) throws Exception {
        for (int i = 0; i < specs.length(); i++) {
            JSONObject jsonResource = specs.getJSONObject(i);
            String name = jsonResource.getString("name");
            String file = specsPath + "/text/" + name;
            String data = StreamUtils.readStream(new FileInputStream(file));
            resourceModel.saveTextResource(name,  jsonResource.getString("package"),
                    data, jsonResource.getString("role"));
        }
    }

    private void processResource(String specsPath, JSONObject resource) throws Exception {
        processBinaryResource(specsPath, resource.getJSONArray("binary"));
        processTextResource(specsPath, resource.getJSONArray("text"));
        processArrayResource(specsPath, resource.getJSONArray("array"));
        processMapResource(specsPath, resource.getJSONArray("map"));
    }

    private void processComponents(JSONObject component, boolean doContainer) throws Exception {
        boolean isContainer = component.getBoolean("isContainer");
        String name = component.getString("name");
        if (isContainer && doContainer) {
            pageModel.createContainer(name,
                    component.getString("package"),
                    component.getJSONArray("js"),
                    component.getString("layout"),
                    component.getJSONArray("components"));
        } else if (!isContainer && !doContainer) {
            JSONArray specs = component.getJSONArray("specs");
            pageModel.createElement(name,
                    component.getString("package"),
                    component.getJSONArray("js"),
                    component.getString("type"),
                    specs);
        }
    }


}
