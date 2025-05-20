package org.abx.webappgen.utils;


import org.abx.webappgen.persistence.ResourceModel;
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
import java.util.Collection;

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
        JSONObject specs = new JSONObject();
        specs.put("methods", getMethods(specsFolder));
        specs.put("users", createUsers(specsFolder));
        specs.put("resources", createResources(specsFolder));
        specs.put("components", createComponents());
        specs.put("pages", createPages());
        new FileOutputStream(specsFolder + "/specs.json").write(specs.toString(1).getBytes());
    }

    public byte[] exportSpecs() throws IOException {
        Path p = Files.createTempDirectory("temp-");
        createSpecs(p.toString());
        byte[] bytes = ZipUtils.zipFolderToByteArray(p);
        ZipUtils.delete(p);
        return bytes;
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


    public JSONArray createComponents() {
        JSONArray jsonComponents = new JSONArray();

        for (org.abx.webappgen.persistence.model.Component component : componentRepository.findAll()) {

            jsonComponents.put(createComponent(component));
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

    public JSONArray createPages() {
        JSONArray jsonPages = new JSONArray();
        for (Page page : pageRepository.findAll()) {
            JSONObject jsonPage = new JSONObject();
            jsonPages.put(jsonPage);
            jsonPage.put("name", page.pageName);
            jsonPage.put("title", page.pageTitle);
            jsonPage.put("role", page.role);
            jsonPage.put("component", page.component.componentName);
        }
        return jsonPages;

    }

    public JSONArray getMethods(String specsFolder) throws IOException {
        new File(specsFolder + "/methods").mkdirs();
        JSONArray methods = new JSONArray();
        for (MethodSpec method : methodSpecRepository.findAll()) {
            JSONObject jsonMethod = new JSONObject();
            jsonMethod.put("name", method.methodName);
            jsonMethod.put("type", method.type);
            jsonMethod.put("outputName", method.outputName);
            jsonMethod.put("role", method.role);
            jsonMethod.put("description", method.description);
            methods.put(jsonMethod);
            new FileOutputStream(specsFolder + "/methods/" + method.methodName + ".js").write(method.methodJS.getBytes());
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
        for (BinaryResource binaryResource : binaryResourceRepository.findAll()) {
            JSONObject jsonBinaryResource = new JSONObject();
            binaryResources.put(jsonBinaryResource);
            jsonBinaryResource.put("name", binaryResource.resourceName);
            jsonBinaryResource.put("contentType", binaryResource.contentType);
            jsonBinaryResource.put("role", binaryResource.role);
            new FileOutputStream(specsFolder + "/binary/" + binaryResource.resourceName).
                    write(binaryResource.resourceValue);
        }
        return binaryResources;
    }

    public JSONArray createTextResources(String specsFolder) throws IOException {
        new File(specsFolder + "/text").mkdirs();
        JSONArray textResources = new JSONArray();
        for (TextResource textResource : textResourceRepository.findAll()) {
            JSONObject jsonTextResource = new JSONObject();
            textResources.put(jsonTextResource);
            jsonTextResource.put("name", textResource.resourceName);
            jsonTextResource.put("role", textResource.role);
            new FileOutputStream(specsFolder + "/text/" + textResource.resourceName).
                    write(textResource.resourceValue.getBytes());
        }
        return textResources;
    }


    public JSONArray createArrayResources(String specsFolder) throws IOException {
        new File(specsFolder + "/array").mkdirs();
        JSONArray arrayResources = new JSONArray();
        for (ArrayResource arrayResource : arrayResourceRepository.findAll()) {
            String name = arrayResource.resourceName;
            arrayResources.put(name);
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
        for (MapResource arrayResource : mapResourceRepository.findAll()) {
            String name = arrayResource.resourceName;
            mapResources.put(name);
            JSONObject values = new JSONObject();
            for (MapEntry entry : arrayResource.resourceEntries) {
                values.put(entry.entryName,entry.value);
            }
            new FileOutputStream(specsFolder + "/map/" + name + ".json").
                    write(values.toString().getBytes());
        }
        return mapResources;
    }


}
