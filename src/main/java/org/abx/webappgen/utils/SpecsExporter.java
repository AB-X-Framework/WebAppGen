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

import static org.abx.webappgen.utils.ElementUtils.defaultPackage;
import static org.abx.webappgen.utils.ElementUtils.elementHashCode;

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
    public ArrayPairResourceRepository arrayPairResourceRepository;

    @Autowired
    public MapResourceRepository mapResourceRepository;

    @Autowired
    public MethodSpecRepository methodSpecRepository;

    @Autowired
    private ArrayEntryRepository arrayEntryRepository;

    @Autowired
    private ArrayPairEntryRepository arrayPairEntryRepository;


    /**
     * Create specs and saves it in
     *
     * @return
     */
    @Transactional
    public void createSpecs(String specsFolder, boolean removeDefaults) throws IOException {
        new File(specsFolder).mkdirs();
        deleteFolderRecursively(new File(specsFolder));
        new File(specsFolder).mkdirs();
        JSONObject specs = new JSONObject();
        specs.put("methods", getMethods(specsFolder, removeDefaults));
        specs.put("users", createUsers(specsFolder));
        specs.put("resources", createResources(specsFolder, removeDefaults));
        specs.put("components", createComponents(specsFolder, removeDefaults));
        specs.put("pages", createPages(specsFolder, removeDefaults));
        new FileOutputStream(specsFolder + "/specs.json").write(specs.toString(2).getBytes());
    }

    public byte[] exportSpecs(boolean removeDefault) throws IOException {
        Path p = Files.createTempDirectory("temp-");
        createSpecs(p.toString(), removeDefault);
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

    public void saveSpecs(String name, boolean removeDefauls) throws IOException {
        Path p = Paths.get(name);
        createSpecs(p.toString(), removeDefauls);
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
        new FileOutputStream(specsFolder + "/" + usersFile).write(users.toString(2).getBytes());
        return usersFile;
    }


    public JSONArray createComponents(String specsFolder, boolean removeDefaults) throws IOException {
        new File(specsFolder + "/components").mkdirs();
        JSONArray jsonComponents = new JSONArray();
        for (String packageName : componentRepository.findDistinctPackageNames()) {
            if (removeDefaults && packageName.startsWith(defaultPackage)) {
                continue;
            }
            jsonComponents.put(packageName);
            JSONArray componentsByPackage = new JSONArray();
            for (org.abx.webappgen.persistence.model.Component component : componentRepository.findAllByPackageName(packageName)) {
                JSONObject jsonComponent = getComponentDetails(component, false);
                componentsByPackage.put(jsonComponent);
            }
            new FileOutputStream(specsFolder + "/components/" + packageName + ".json").
                    write(componentsByPackage.toString(2).getBytes());
        }
        return jsonComponents;
    }

    @Transactional
    public JSONObject getComponentDetails(String componentName, boolean includePackage) {
        return getComponentDetails(componentRepository.findByComponentId(
                elementHashCode(componentName)), includePackage);
    }

    private JSONObject getComponentDetails(
            org.abx.webappgen.persistence.model.Component component,
            boolean includePackage) {
        JSONObject jsonComponent = new JSONObject();
        jsonComponent.put("name", component.componentName);
        jsonComponent.put("isContainer", component.isContainer);
        if (includePackage) {
            jsonComponent.put("package", component.packageName);
        }
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
                jsonEnvValue.put("value", new JSONObject(envValue.envValue));

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
            jsonEnvValue.put("value", envValue.envValue);
        }
        return jsonValues;
    }

    public JSONArray createPages(String specsFolder, boolean removeDefaults) throws IOException {
        new File(specsFolder + "/pages").mkdirs();
        JSONArray jsonPages = new JSONArray();
        HashMap<String, JSONArray> pages = new HashMap<>();
        for (Page page : pageRepository.findAll()) {
            if (removeDefaults && page.packageName.startsWith(defaultPackage)) {
                continue;
            }
            JSONObject jsonPage = getPageDetails(page, false);
            if (!pages.containsKey(page.packageName)) {
                jsonPages.put(page.packageName);
                pages.put(page.packageName, new JSONArray());
            }
            pages.get(page.packageName).put(jsonPage);
        }
        for (Map.Entry<String, JSONArray> page : pages.entrySet()) {
            new FileOutputStream(specsFolder + "/pages/" + page.getKey() + ".json").
                    write(page.getValue().toString(2).getBytes());
        }
        return jsonPages;

    }

    @Transactional
    public JSONObject getPageDetails(String page) {
        return getPageDetails(pageRepository.findByPageId(elementHashCode(page)), true);
    }

    private JSONObject getPageDetails(Page page, boolean packageName) {
        JSONObject jsonPage = new JSONObject();
        jsonPage.put("name", page.pageName);
        jsonPage.put("matches", page.matches);
        jsonPage.put("title", envValue(page.pageTitle) );
        jsonPage.put("role", page.role);
        jsonPage.put("component", page.component.componentName);
        jsonPage.put("css", envValue(page.css));
        jsonPage.put("scripts", envValue(page.scripts));
        if (packageName) {
            jsonPage.put("componentPackage", page.component.packageName);
            jsonPage.put("package", page.packageName);
        }
        return jsonPage;
    }

    public JSONArray getMethods(String specsFolder, boolean removeDefaults) throws IOException {
        new File(specsFolder + "/methods").mkdirs();
        JSONArray methods = new JSONArray();
        for (String packageName : methodSpecRepository.findDistinctPackageNames()) {
            if (removeDefaults && packageName.startsWith(defaultPackage)) {
                continue;
            }
            methods.put(packageName);
            new File(specsFolder + "/methods/" + packageName).mkdirs();
            JSONArray jsonMethodsPerPackage = new JSONArray();
            for (MethodSpec method : methodSpecRepository.findAllByPackageName(packageName)) {
                JSONObject jsonMethod = new JSONObject();
                jsonMethodsPerPackage.put(jsonMethod);
                jsonMethod.put("name", method.methodName);
                jsonMethod.put("type", method.type);
                jsonMethod.put("outputName", method.outputName);
                jsonMethod.put("role", method.role);
                jsonMethod.put("description", method.description);
                new File(specsFolder + "/methods/" + packageName.replace(".","/")).mkdirs();
                new FileOutputStream(specsFolder + "/methods/"+
                        method.methodName.replace('.','/') + ".js").write(method.methodJS.getBytes());
            }
            new FileOutputStream(specsFolder + "/methods/" +
                    packageName + ".json").write(jsonMethodsPerPackage.toString(2).getBytes());
        }

        return methods;
    }

    public JSONObject createResources(String specsFolder, boolean removeDefaults) throws IOException {
        JSONObject object = new JSONObject();
        object.put("binary", createBinaryResources(specsFolder, removeDefaults));
        object.put("text", createTextResources(specsFolder, removeDefaults));
        object.put("array", createArrayResources(specsFolder, removeDefaults));
        object.put("arrayPair", createArrayPairResources(specsFolder, removeDefaults));
        object.put("map", createMapResources(specsFolder, removeDefaults));
        return object;
    }

    public JSONArray createBinaryResources(String specsFolder, boolean removeDefaults) throws IOException {
        new File(specsFolder + "/binary").mkdirs();
        JSONArray binaryResources = new JSONArray();

        for (String packageName : binaryResourceRepository.findDistinctPackageNames()) {
            if (removeDefaults && packageName.startsWith(defaultPackage)) {
                continue;
            }
            binaryResources.put(packageName);
            JSONArray packageResources = new JSONArray();
            new File(specsFolder + "/binary/" + packageName).mkdirs();
            for (BinaryResource binaryResource : binaryResourceRepository.findAllByPackageName(packageName)) {
                JSONObject jsonBinaryResource = new JSONObject();
                packageResources.put(jsonBinaryResource);
                jsonBinaryResource.put("name", binaryResource.resourceName);
                jsonBinaryResource.put("contentType", binaryResource.contentType);
                jsonBinaryResource.put("access", binaryResource.access);
                jsonBinaryResource.put("owner",userRepository.findByUserId(binaryResource.owner).username);
                new File(specsFolder+"/binary/" + packageName.replace(".","/")).mkdirs();
                new FileOutputStream(specsFolder + "/binary/" + binaryResource.resourceName).
                        write(binaryResource.resourceValue);
            }
            new FileOutputStream(specsFolder + "/binary/" + packageName + ".json").
                    write(packageResources.toString(2).getBytes());
        }
        return binaryResources;
    }

    public JSONArray createTextResources(String specsFolder, boolean removeDefaults) throws IOException {
        new File(specsFolder + "/text").mkdirs();
        JSONArray textResources = new JSONArray();
        for (String packageName : textResourceRepository.findDistinctPackageNames()) {
            if (removeDefaults && packageName.startsWith(defaultPackage)) {
                continue;
            }
            new File(specsFolder + "/text/" + packageName).mkdirs();
            textResources.put(packageName);
            JSONArray packageResources = new JSONArray();
            for (TextResource textResource : textResourceRepository.findAllByPackageName(packageName)) {
                JSONObject jsonTextResource = new JSONObject();
                packageResources.put(jsonTextResource);
                jsonTextResource.put("name", textResource.resourceName);
                jsonTextResource.put("package", packageName);
                jsonTextResource.put("access", textResource.access);
                jsonTextResource.put("title", textResource.title);
                jsonTextResource.put("owner", userRepository.findByUserId(textResource.owner).username);
                new File(specsFolder+"/text/" + packageName.replace(".","/")).mkdirs();
                new FileOutputStream(specsFolder + "/text/" +
                        textResource.resourceName.replace('.','/') + ".txt").
                        write(textResource.resourceValue.getBytes());
            }

            new FileOutputStream(specsFolder + "/text/" + packageName + ".json").
                    write(packageResources.toString(2).getBytes());
        }
        return textResources;
    }

    public JSONArray createArrayPairResources(String specsFolder, boolean removeDefaults) throws IOException {
        new File(specsFolder + "/arrayPair").mkdirs();
        JSONArray arrayPairResources = new JSONArray();
        for (ArrayPairResource arrayPairResource : arrayPairResourceRepository.findAll()) {
            if (removeDefaults && arrayPairResource.packageName.startsWith(defaultPackage)) {
                continue;
            }
            JSONObject jsonArrayPairResource = new JSONObject();
            arrayPairResources.put(jsonArrayPairResource);
            String name = arrayPairResource.resourceName;
            long id = elementHashCode(name);
            jsonArrayPairResource.put("name", name);
            jsonArrayPairResource.put("package", arrayPairResource.packageName);
            jsonArrayPairResource.put("owner", userRepository.findByUserId(arrayPairResource.owner).username);
            jsonArrayPairResource.put("access", arrayPairResource.access);
            JSONArray values = new JSONArray();
            for (ArrayPairEntry entry : arrayPairEntryRepository.findAllByArrayPairResourceId(id)) {
                JSONObject jsonEntry = new JSONObject();
                jsonEntry.put("key",entry.arrayPairKey);
                jsonEntry.put("value",entry.arrayPairValue);
                values.put(jsonEntry);
            }
            new FileOutputStream(specsFolder + "/arrayPair/" + name + ".json").
                    write(values.toString().getBytes());
        }
        return arrayPairResources;
    }

    public JSONArray createArrayResources(String specsFolder, boolean removeDefaults) throws IOException {
        new File(specsFolder + "/array").mkdirs();
        JSONArray arrayResources = new JSONArray();
        for (ArrayResource arrayResource : arrayResourceRepository.findAll()) {
            if (removeDefaults && arrayResource.packageName.startsWith(defaultPackage)) {
                continue;
            }
            JSONObject jsonArrayResource = new JSONObject();
            arrayResources.put(jsonArrayResource);
            String name = arrayResource.resourceName;
            long id = elementHashCode(name);
            jsonArrayResource.put("name", name);
            jsonArrayResource.put("package", arrayResource.packageName);
            jsonArrayResource.put("owner", userRepository.findByUserId(arrayResource.owner).username);
            jsonArrayResource.put("access", arrayResource.access);
            JSONArray values = new JSONArray();
            for (ArrayEntry entry : arrayEntryRepository.findAllByArrayResourceId(id)) {
                values.put(entry.arrayValue);
            }
            new FileOutputStream(specsFolder + "/array/" + name + ".json").
                    write(values.toString().getBytes());
        }
        return arrayResources;
    }

    public JSONArray createMapResources(String specsFolder, boolean removeDefaults) throws IOException {
        new File(specsFolder + "/map").mkdirs();
        JSONArray mapResources = new JSONArray();
        for (MapResource mapResource : mapResourceRepository.findAll()) {
            if (removeDefaults && mapResource.packageName.startsWith(defaultPackage)) {
                continue;
            }
            JSONObject jsonMapResource = new JSONObject();
            mapResources.put(jsonMapResource);
            String name = mapResource.resourceName;
            jsonMapResource.put("name", name);
            jsonMapResource.put("package", mapResource.packageName);
            jsonMapResource.put("owner", userRepository.findByUserId(mapResource.owner).username);
            jsonMapResource.put("access", mapResource.access);
            JSONObject values = new JSONObject();
            for (MapEntry entry : mapResource.resourceEntries) {
                values.put(entry.entryName, entry.mapValue);
            }
            new FileOutputStream(specsFolder + "/map/" + name + ".json").
                    write(values.toString().getBytes());
        }
        return mapResources;
    }


}
