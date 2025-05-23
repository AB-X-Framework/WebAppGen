package org.abx.webappgen.utils;


import org.abx.webappgen.persistence.dao.*;
import org.abx.webappgen.persistence.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class SpecsExporter {
    @Autowired
    public UserRepository userRepository;

    @Autowired
    public PageRepository pageRepository;

    @Autowired
    public ComponentRepository componentRepository;

    @Autowired
    public TextResourceRepository textResourceRepository;

    @Autowired
    public BinaryResourceRepository binaryResourceRepository;

    @Autowired
    public ArrayResourceRepository arrayResourceRepository;

    @Autowired
    public MapResourceRepository mapResourceRepository;

    @Autowired
    public MethodSpecRepository methodSpecRepository;


    /**
     * Create specs and saves it in
     *
     * @return
     */
    @Transactional
    public void createSpecs(String specsFolder) throws IOException {
        deleteFolderRecursively(new File(specsFolder));
        JSONObject specs = new JSONObject();
        specs.put("methods", getMethods(specsFolder));
        specs.put("users", createUsers(specsFolder));
        specs.put("resources", createResources(specsFolder));
        specs.put("components", createComponents(specsFolder));
        specs.put("pages", createPages(specsFolder));
        new FileOutputStream(specsFolder + "/specs.json").write(specs.toString(1).getBytes());
    }

    public byte[] exportSpecs() throws IOException {
        Path p = Files.createTempDirectory("temp-");
        createSpecs(p.toString());
        byte[] bytes = ZipUtils.zipFolderToByteArray(p);
        ZipUtils.delete(p);
        return bytes;
    }

    public static void deleteFolderRecursively(File folder) throws IOException {
        if (!folder.exists()) {
            throw new IOException("Folder does not exist: " + folder.getAbsolutePath());
        }

        if (!folder.isDirectory()) {
            throw new IOException("Not a directory: " + folder.getAbsolutePath());
        }

        File[] files = folder.listFiles();
        if (files != null) { // folder is not empty
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolderRecursively(file);
                } else {
                    if (!file.delete()) {
                        throw new IOException("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
        }

        if (!folder.delete()) {
            throw new IOException("Failed to delete folder: " + folder.getAbsolutePath());
        }
    }

    public void saveSpecs(String name) throws IOException {
        Path p = Paths.get(name);
        createSpecs(p.toString());
    }

    public String createUsers(String specsFolder) throws IOException {
        JSONArray users = new JSONArray();
        for (User user : userRepository.findAll()) {
            JSONObject jsonUser = new JSONObject();
            users.put(jsonUser);
            jsonUser.put("username", user.username);
            jsonUser.put("role", user.role);
            jsonUser.put("enabled", user.enabled);
            jsonUser.put("password", user.password);
        }
        String usersFile = "users.json";
        new FileOutputStream(specsFolder + "/" + usersFile).write(users.toString(1).getBytes());
        return usersFile;
    }


    public JSONArray createComponents(String specsFolder) throws IOException {
        new File(specsFolder + "/components").mkdirs();
        JSONArray jsonComponents = new JSONArray();
        for (String packageName : componentRepository.findDistinctPackageNames()) {
            jsonComponents.put(packageName);
            JSONArray componentsByPackage = new JSONArray();
            for (org.abx.webappgen.persistence.model.Component component : componentRepository.findAllByPackageName(packageName)) {
                JSONObject jsonComponent = createComponent(component);
                componentsByPackage.put(jsonComponent);
            }
            new FileOutputStream(specsFolder + "/components/" + packageName + ".json").
                    write(componentsByPackage.toString(1).getBytes());
        }
        return jsonComponents;
    }

    private JSONObject createComponent(org.abx.webappgen.persistence.model.Component component) {
        JSONObject jsonComponent = new JSONObject();
        jsonComponent.put("name", component.componentName);
        jsonComponent.put("isContainer", component.isContainer);
        jsonComponent.put("js", envValue(component.js));
        if (component.isContainer) {
            processContainer(jsonComponent, component.container);
        } else {
            Element element = component.element;
            jsonComponent.put("type", element.type);
            JSONArray jsonSpecs = new JSONArray();
            jsonComponent.put("specs", jsonSpecs);
            for (EnvValue envValue : element.specs) {
                JSONObject jsonEnvValue = new JSONObject();
                jsonSpecs.put(jsonEnvValue);
                jsonEnvValue.put("env", envValue.env);
                jsonEnvValue.put("value", new JSONObject(envValue.value));

            }
        }
        return jsonComponent;
    }

    private void processContainer(JSONObject jsonComponent, Container container) {
        jsonComponent.put("layout", container.layout);
        JSONArray components = new JSONArray();
        jsonComponent.put("components", components);
        for (InnerComponent inner : container.innerComponent) {
            JSONObject jsonInner = new JSONObject();
            components.put(jsonInner);
            jsonInner.put("env", inner.env);
            jsonInner.put("size", inner.size);
            jsonInner.put("innerId", inner.innerId);
            jsonInner.put("component", inner.child.componentName);
        }
    }

    private JSONArray envValue(Collection<EnvValue> values) {
        JSONArray jsonValues = new JSONArray();
        for (EnvValue envValue : values) {
            JSONObject jsonEnvValue = new JSONObject();
            jsonValues.put(jsonEnvValue);
            jsonEnvValue.put("env", envValue.env);
            jsonEnvValue.put("value", envValue.value);
        }
        return jsonValues;
    }

    public JSONArray createPages(String specsFolder) throws IOException {
        new File(specsFolder + "/pages").mkdirs();
        JSONArray jsonPages = new JSONArray();
        HashMap<String, JSONArray> pages = new HashMap<>();
        for (Page page : pageRepository.findAll()) {
            JSONObject jsonPage = new JSONObject();
            jsonPage.put("name", page.pageName);
            jsonPage.put("matches", page.matches);
            jsonPage.put("matchesId", page.matchesId);
            jsonPage.put("title", page.pageTitle);
            jsonPage.put("role", page.role);
            jsonPage.put("component", page.component.componentName);
            jsonPage.put("css", envValue(page.css));
            jsonPage.put("scripts", envValue(page.scripts));
            if (!pages.containsKey(page.packageName)) {
                jsonPages.put(page.packageName);
                pages.put(page.packageName, new JSONArray());
            }
            pages.get(page.packageName).put(jsonPage);
        }
        for (Map.Entry<String, JSONArray> page : pages.entrySet()) {
            new FileOutputStream(specsFolder + "/pages/" + page.getKey() + ".json").
                    write(page.getValue().toString(1).getBytes());
        }
        return jsonPages;

    }

    public JSONArray getMethods(String specsFolder) throws IOException {
        new File(specsFolder + "/methods").mkdirs();

        JSONArray methods = new JSONArray();
        for (String packageName : methodSpecRepository.findDistinctPackageNames()) {
            methods.put(packageName);
            new File(specsFolder + "/methods/" + packageName).mkdirs();
            JSONArray jsonMethodsPerPackage = new JSONArray();
            for (MethodSpec method : methodSpecRepository.findAll()) {
                JSONObject jsonMethod = new JSONObject();
                jsonMethodsPerPackage.put(jsonMethod);
                jsonMethod.put("name", method.methodName);
                jsonMethod.put("package", method.packageName);
                jsonMethod.put("type", method.type);
                jsonMethod.put("outputName", method.outputName);
                jsonMethod.put("role", method.role);
                jsonMethod.put("description", method.description);
                new FileOutputStream(specsFolder + "/methods/" +
                        packageName + "/" + method.methodName + ".js").write(method.methodJS.getBytes());
            }
            new FileOutputStream(specsFolder + "/methods/" +
                    packageName + ".json").write(jsonMethodsPerPackage.toString(1).getBytes());
        }

        return methods;
    }

    public JSONObject createResources(String specsFolder) throws IOException {
        JSONObject object = new JSONObject();
        object.put("binary", createBinaryResources(specsFolder));
        object.put("text", createTextResources(specsFolder));
        object.put("array", createArrayResources(specsFolder));
        object.put("map", createMapResources(specsFolder));
        return object;
    }

    public JSONArray createBinaryResources(String specsFolder) throws IOException {
        new File(specsFolder + "/binary").mkdirs();
        JSONArray binaryResources = new JSONArray();

        for (String packageName : binaryResourceRepository.findDistinctPackageNames()) {
            binaryResources.put(packageName);
            JSONArray packageResources = new JSONArray();
            new File(specsFolder + "/binary/" + packageName).mkdirs();
            for (BinaryResource binaryResource : binaryResourceRepository.findAllByPackageName(packageName)) {
                JSONObject jsonBinaryResource = new JSONObject();
                packageResources.put(jsonBinaryResource);
                jsonBinaryResource.put("name", binaryResource.resourceName);
                jsonBinaryResource.put("package", packageName);
                jsonBinaryResource.put("contentType", binaryResource.contentType);
                jsonBinaryResource.put("role", binaryResource.role);
                new FileOutputStream(specsFolder + "/binary/" + packageName + "/" + binaryResource.resourceName).
                        write(binaryResource.resourceValue);
            }
            new FileOutputStream(specsFolder + "/binary/" + packageName + ".json").
                    write(packageResources.toString(1).getBytes());
        }
        return binaryResources;
    }

    public JSONArray createTextResources(String specsFolder) throws IOException {
        new File(specsFolder + "/text").mkdirs();
        JSONArray textResources = new JSONArray();
        for (String packageName : textResourceRepository.findDistinctPackageNames()) {
            new File(specsFolder + "/text/" + packageName).mkdirs();
            textResources.put(packageName);
            JSONArray packageResources = new JSONArray();
            for (TextResource textResource : textResourceRepository.findAllByPackageName(packageName)) {
                JSONObject jsonTextResource = new JSONObject();
                packageResources.put(jsonTextResource);
                jsonTextResource.put("name", textResource.resourceName);
                jsonTextResource.put("package", packageName);
                jsonTextResource.put("role", textResource.role);
                new FileOutputStream(specsFolder + "/text/" + packageName + "/" + textResource.resourceName).
                        write(textResource.resourceValue.getBytes());
            }

            new FileOutputStream(specsFolder + "/text/" + packageName + ".json").
                    write(packageResources.toString(1).getBytes());
        }
        return textResources;
    }


    public JSONArray createArrayResources(String specsFolder) throws IOException {
        new File(specsFolder + "/array").mkdirs();
        JSONArray arrayResources = new JSONArray();
        for (ArrayResource arrayResource : arrayResourceRepository.findAll()) {
            JSONObject jsonArrayResource = new JSONObject();
            arrayResources.put(jsonArrayResource);
            String name = arrayResource.resourceName;
            jsonArrayResource.put("name", name);
            jsonArrayResource.put("package", arrayResource.packageName);
            JSONArray values = new JSONArray();
            for (ArrayEntry entry : arrayResource.resourceEntries) {
                values.put(entry.value);
            }
            new FileOutputStream(specsFolder + "/array/" + name + ".json").
                    write(values.toString().getBytes());
        }
        return arrayResources;
    }

    public JSONArray createMapResources(String specsFolder) throws IOException {
        new File(specsFolder + "/map").mkdirs();
        JSONArray mapResources = new JSONArray();
        for (MapResource mapResource : mapResourceRepository.findAll()) {
            JSONObject jsonMapResource = new JSONObject();
            mapResources.put(jsonMapResource);
            String name = mapResource.resourceName;
            jsonMapResource.put("name", name);
            jsonMapResource.put("package", mapResource.packageName);
            JSONObject values = new JSONObject();
            for (MapEntry entry : mapResource.resourceEntries) {
                values.put(entry.entryName, entry.value);
            }
            new FileOutputStream(specsFolder + "/map/" + name + ".json").
                    write(values.toString().getBytes());
        }
        return mapResources;
    }


}
