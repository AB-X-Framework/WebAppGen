package org.abx.webappgen.utils;


import org.abx.webappgen.persistence.ResourceModel;
import org.abx.webappgen.persistence.dao.*;
import org.abx.webappgen.persistence.model.BinaryResource;
import org.abx.webappgen.persistence.model.MethodSpec;
import org.abx.webappgen.persistence.model.TextResource;
import org.abx.webappgen.persistence.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class SpecsExporter {
    @Autowired
    public UserRepository userRepository;

    @Autowired
    public PageRepository pageRepository;

    @Autowired
    public ComponentRepository componentRepository;

    @Autowired
    public ContainerRepository containerRepository;

    @Autowired
    public ElementRepository elementRepository;

    @Autowired
    public InnerComponentRepository innerComponentRepository;

    @Autowired
    public EnvValueRepository envValueRepository;

    @Autowired
    public TextResourceRepository textResourceRepository;

    @Autowired
    public BinaryResourceRepository binaryResourceRepository;


    @Autowired
    public ArrayResourceRepository arrayResourceRepository;

    @Autowired
    public ArrayEntryRepository arrayEntryRepository;

    @Autowired
    public MapEntryRepository mapEntryRepository;

    @Autowired
    public MapResourceRepository mapResourceRepository;

    @Autowired
    public MethodSpecRepository methodSpecRepository;

    @Autowired
    public ResourceModel resourceModel;

    /**
     * Create specs and saves it in
     *
     * @return
     */
    public void createSpecs(String specsFolder) throws IOException {
        JSONObject specs = new JSONObject();
        specs.put("methods", getMethods(specsFolder));
        specs.put("users", createUsers(specsFolder));
        specs.put("resources", createResources(specsFolder));
        specs.put("components", createComponents());
        specs.put("pages", createPages());
        new FileOutputStream(specsFolder + "/specs.json").write(specs.toString(1).getBytes());
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


    public JSONArray createComponents()  {
        JSONArray jsonComponents = new JSONArray();

        return jsonComponents;
    }

    public JSONArray createPages()  {
        JSONArray jsonPages = new JSONArray();

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
            methods.put(jsonMethod);
            new FileOutputStream(specsFolder + "/methods/" + method.methodName).write(method.methodJS.getBytes());
        }
        return methods;
    }

    public JSONObject createResources(String specsFolder) throws IOException {
        JSONObject object = new JSONObject();
        object.put("binary",createBinaryResources(specsFolder));
        return object;
    }

    public JSONArray createBinaryResources(String specsFolder) throws IOException {
        new File(specsFolder + "/binary").mkdirs();
        JSONArray binaryResources = new JSONArray();
        for (BinaryResource binaryResource : binaryResourceRepository.findAll()) {
            JSONObject jsonBinaryResource = new JSONObject();
            binaryResources.put(jsonBinaryResource);
            jsonBinaryResource.put("name",binaryResource.resourceName);
            jsonBinaryResource.put("contentType",binaryResource.contentType);
            jsonBinaryResource.put("role",binaryResource.role);
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
            jsonTextResource.put("name",textResource.resourceName);
            jsonTextResource.put("role",textResource.role);
            new FileOutputStream(specsFolder + "/text/" + textResource.resourceName).
                    write(textResource.resourceValue.getBytes());
        }
        return textResources;
    }


}
