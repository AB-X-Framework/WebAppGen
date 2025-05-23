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
import java.util.HashSet;

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
            loadSpecs(appSpecsPath);
        }
    }

    public boolean uploadBinarySpecs(byte[] zipFolder) throws Exception {
        String path = ZipUtils.unzipToTempFolder(zipFolder);
        loadSpecs(path);
        return true;
    }

    /**
     * Uploads spe
     *
     * @param specsFolder
     * @throws Exception
     */
    public void loadSpecs(String specsFolder) throws Exception {
        File resourceFile = new File(specsFolder + "/specs.json");
        String data = StreamUtils.readStream(new FileInputStream(resourceFile));
        JSONObject obj = new JSONObject(data);
        processUsers(specsFolder, obj.getString("users"));
        JSONArray methods = obj.getJSONArray("methods");
        for (int i = 0; i < methods.length(); i++) {
            String packageName = methods.getString(i);
            processPackageMethods(specsFolder, packageName);
        }
        JSONObject resources = obj.getJSONObject("resources");
        processResource(specsFolder, resources);
        JSONArray components = obj.getJSONArray("components");
        processComponents(specsFolder, components);
        JSONArray pages = obj.getJSONArray("pages");
        processPages(specsFolder, pages);
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

    private void processPages(String specsFolder, JSONArray pages) throws IOException {
        String pagesFolder = specsFolder + "/pages";
        for (int i = 0; i < pages.length(); i++) {
            String packageName = pages.getString(i);
            processPagePackage(pagesFolder, packageName);
        }
    }

    private void processPagePackage(String pagesFolder, String packageName) throws IOException {
        JSONArray pages = new JSONArray(StreamUtils.readStream(new FileInputStream(
                pagesFolder + "/" + packageName + ".json"
        )));
        for (int i = 0; i < pages.length(); ++i) {
            processPage(packageName, pages.getJSONObject(i));
        }
    }

    private void processPage(String packageName, JSONObject page) {
        pageModel.createPageWithPageName(
                page.getString("name"),
                packageName,
                page.getString("matches"),
                page.getString("title"),
                page.getString("role"),
                page.getString("component"),
                page.getJSONArray("css"),
                page.getJSONArray("scripts"));

    }

    private void processPackageMethods(String specsFolder, String packageName) throws IOException {
        JSONArray jsonMethods = new JSONArray(
                StreamUtils.readStream(new FileInputStream(specsFolder + "/methods/" + packageName + ".json"))
        );
        for (int i = 0; i < jsonMethods.length(); i++) {
            JSONObject method = jsonMethods.getJSONObject(i);
            processMethods(specsFolder, packageName, method);
        }
    }

    private void processMethods(String specsFolder, String packageName, JSONObject method) throws IOException {
        MethodSpec specs = new MethodSpec();
        specs.methodName = method.getString("name");
        specs.packageName = packageName;
        specs.description = method.getString("description");
        specs.methodSpecId = PageModel.elementHashCode(specs.methodName);
        specs.methodJS = StreamUtils.readStream(new FileInputStream(
                specsFolder + "/methods/" + packageName + "/" + specs.methodName + ".js"));
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
            resourceModel.saveMapResource(mapName, mapResource.getString("package"), new JSONObject(arrayData));
        }
    }

    private void processBinaryResource(String specsPath, JSONArray packageNames) throws Exception {
        for (int packageNameIndex = 0; packageNameIndex < packageNames.length(); packageNameIndex++) {
            String packageName = packageNames.getString(packageNameIndex);
            JSONArray specs = new JSONArray(
                    StreamUtils.readStream(new FileInputStream(
                            specsPath + "/binary/" + packageName + ".json")));
            for (int i = 0; i < specs.length(); i++) {
                JSONObject jsonResource = specs.getJSONObject(i);
                String name = jsonResource.getString("name");
                String file = specsPath + "/binary/" + packageName + "/" + name;
                byte[] data = StreamUtils.readByteArrayStream(new FileInputStream(file));
                resourceModel.saveBinaryResource(name, packageName,
                        jsonResource.getString("contentType"), data, jsonResource.getString("role"));
            }
        }

    }

    private void processTextResource(String specsPath, JSONArray specs) throws Exception {
        for (int i = 0; i < specs.length(); i++) {
            String packageName = specs.getString(i);
            JSONArray textPackages = new JSONArray(
                    StreamUtils.readStream(
                            new FileInputStream(specsPath+"/text/" + packageName + ".json") ));
            for (int j = 0; j < textPackages.length(); j++) {
                JSONObject jsonResource = textPackages.getJSONObject(j);
                String name = jsonResource.getString("name");
                String file = specsPath + "/text/" + packageName+"/"+name;
                String data = StreamUtils.readStream(new FileInputStream(file));
                resourceModel.saveTextResource(name,packageName,
                        data, jsonResource.getString("role"));

            }
        }
    }

    private void processResource(String specsPath, JSONObject resource) throws Exception {
        processBinaryResource(specsPath, resource.getJSONArray("binary"));
        processTextResource(specsPath, resource.getJSONArray("text"));
        processArrayResource(specsPath, resource.getJSONArray("array"));
        processMapResource(specsPath, resource.getJSONArray("map"));
    }

    private void processComponents(String specsFolder, JSONArray componentPackages) throws Exception {
        JSONArray missing = new JSONArray();
        HashSet<String> saved = new HashSet<>();
        for (int i = 0; i < componentPackages.length(); i++) {
            String componentPackage = componentPackages.getString(i);
            JSONArray packageComponents = new JSONArray(
                    StreamUtils.readStream(new FileInputStream(
                            specsFolder + "/components/" + componentPackage + ".json")));
            for (int j = 0; j < packageComponents.length(); j++) {
                packageComponents.getJSONObject(j).put("package", componentPackage);
            }
            missing.putAll(packageComponents);
            missing = processComponentsAux(missing, saved);
        }
        int totalMissing = missing.length();
        while (true) {
            missing = processComponentsAux(missing, saved);
            if (missing.isEmpty()) {
                return;
            }
            if (missing.length() == totalMissing) {
                throw new Exception("Could not resolve all components " + missing.toString(1));
            }
        }
    }

    private JSONArray processComponentsAux(JSONArray components, HashSet<String> saved) throws Exception {
        if (components.isEmpty()) {
            return components;
        }
        JSONArray missing = new JSONArray();
        for (int i = 0; i < components.length(); i++) {
            JSONObject component = components.getJSONObject(i);
            String name = component.getString("name");
            boolean isContainer = component.getBoolean("isContainer");
            if (isContainer) {
                JSONArray children = component.getJSONArray("components");
                boolean valid = true;
                for (int j = 0; j < children.length(); j++) {
                    JSONObject child = children.getJSONObject(j);
                    String childComponent = child.getString("component");
                    if (!saved.contains(childComponent)) {
                        missing.put(component);
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    processComponent(component);
                    saved.add(name);
                }
            } else {
                saved.add(name);
                processComponent(component);
            }
        }
        return missing;
    }

    private void processComponent(JSONObject component) throws Exception {
        boolean isContainer = component.getBoolean("isContainer");
        String name = component.getString("name");
        if (isContainer) {
            pageModel.createContainer(name,
                    component.getString("package"),
                    component.getJSONArray("js"),
                    component.getString("layout"),
                    component.getJSONArray("components"));
        } else {
            JSONArray specs = component.getJSONArray("specs");
            pageModel.createElement(name,
                    component.getString("package"),
                    component.getJSONArray("js"),
                    component.getString("type"),
                    specs);
        }
    }


}
