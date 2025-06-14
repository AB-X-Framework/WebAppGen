package org.abx.webappgen.utils;


import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.abx.util.StreamUtils;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.ResourceModel;
import org.abx.webappgen.persistence.dao.MethodSpecRepository;
import org.abx.webappgen.persistence.dao.PageRepository;
import org.abx.webappgen.persistence.dao.UserRepository;
import org.abx.webappgen.persistence.model.MethodSpec;
import org.abx.webappgen.persistence.model.Page;
import org.abx.webappgen.persistence.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;

import static org.abx.webappgen.utils.ElementUtils.elementHashCode;

@Component
public class SpecsImporter {
    @Autowired
    private PageModel pageModel;

    @Autowired
    private ResourceModel resourceModel;

    @Autowired
    private MethodSpecRepository methodSpecRepository;

    @Value("${load.appFolder}")
    public boolean loadAppFolder;

    @Value("${load.appResources}")
    public boolean loadAppResources;

    @Value("${drop.app}")
    public boolean dropApp;

    @Value("${app.specsFolder}")
    public String specsFolder;


    @Value("${app.specsResources}")
    public String specsResources;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageRepository pageRepository;

    @PostConstruct
    public void init() throws Exception {
        if (dropApp) {
            pageModel.clean();
        }
        if (loadAppResources) {
            JSONArray resources = new JSONArray(specsResources);
            for (int i = 0; i < resources.length(); i++) {
                loadSpecsResources(resources.getString(i));
            }
        }
        if (loadAppFolder) {
            loadSpecsFolder(specsFolder);
        }
    }

    public boolean uploadBinarySpecs(byte[] zipFolder) throws Exception {
        String path = ZipUtils.unzipToTempFolder(zipFolder);
        loadSpecsFolder(path);
        return true;
    }


    public void loadSpecsFolder(String specsFolder) throws Exception {
        loadSpecs(specsFolder, true);
    }


    public void loadSpecsResources(String specsFolder) throws Exception {
        loadSpecs(specsFolder, false);
    }

    public String getData(String resource, boolean fs) throws Exception {
        InputStream inputStream;
        if (fs) {
            File resourceFile = new File(resource);
            inputStream = new FileInputStream(resourceFile);
        } else {
            inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        }
        return StreamUtils.readStream(inputStream);
    }


    public byte[] getBinaryData(String resource, boolean fs) throws Exception {
        InputStream inputStream;
        if (fs) {
            File resourceFile = new File(resource);
            inputStream = new FileInputStream(resourceFile);
        } else {
            inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        }
        return StreamUtils.readByteArrayStream(inputStream);
    }

    /**
     * Uploads spe
     *
     * @param specsPath
     * @throws Exception
     */
    public void loadSpecs(String specsPath, boolean fs) throws Exception {
        String data = getData(specsPath + "/specs.json", fs);
        JSONObject obj = new JSONObject(data);
        processUsers(specsPath, obj.getString("users"), fs);
        JSONArray methods = obj.getJSONArray("methods");
        for (int i = 0; i < methods.length(); i++) {
            String packageName = methods.getString(i);
            processPackageMethods(specsPath, packageName, fs);
        }
        JSONObject resources = obj.getJSONObject("resources");
        processResource(specsPath, resources, fs);
        JSONArray components = obj.getJSONArray("components");
        processComponents(specsPath, components, fs);
        JSONArray pages = obj.getJSONArray("pages");
        processPages(specsPath, pages, fs);
    }

    private void processUsers(String specsFolder, String userfile, boolean fs) throws Exception {
        String users = getData(specsFolder + "/" + userfile, fs);
        JSONArray jsonUsers = new JSONArray(users);
        for (int i = 0; i < jsonUsers.length(); i++) {
            JSONObject jsonUser = jsonUsers.getJSONObject(i);
            User user = new User();
            user.username = jsonUser.getString("username");
            user.userId = elementHashCode(user.username);
            user.password = jsonUser.getString("password");
            user.enabled = jsonUser.getBoolean("enabled");
            user.role = jsonUser.getString("role");
            userRepository.save(user);
        }
    }

    private void processPages(String specsFolder, JSONArray pages, boolean fs) throws Exception {
        String pagesFolder = specsFolder + "/pages";
        for (int i = 0; i < pages.length(); i++) {
            String packageName = pages.getString(i);
            processPagePackage(pagesFolder, packageName, fs);
        }
    }

    private void processPagePackage(String pagesFolder, String packageName, boolean fs) throws Exception {
        String pagesText = getData(pagesFolder + "/" + packageName + ".json", fs);
        JSONArray pages = new JSONArray(pagesText);
        for (int i = 0; i < pages.length(); ++i) {
            processPage(packageName, pages.getJSONObject(i));
        }
    }

    public void processPage(String packageName, JSONObject page) {
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

    private void processPackageMethods(String specsFolder, String packageName, boolean fs) throws Exception {

        String methods = getData(specsFolder + "/methods/" + packageName + ".json", fs);
        JSONArray jsonMethods = new JSONArray(methods);
        for (int i = 0; i < jsonMethods.length(); i++) {
            JSONObject method = jsonMethods.getJSONObject(i);
            processMethods(specsFolder, packageName, method, fs);
        }
    }

    private void processMethods(String specsFolder, String packageName, JSONObject method, boolean fs)
            throws Exception {
        MethodSpec specs = new MethodSpec();
        specs.methodName = method.getString("name");
        specs.packageName = packageName;
        specs.description = method.getString("description");
        specs.methodSpecId = elementHashCode(specs.methodName);
        specs.methodJS = getData(specsFolder + "/methods/" +
                specs.methodName.replace('.', '/') + ".js", fs);
        specs.type = method.getString("type");
        specs.outputName = method.getString("outputName");
        specs.role = method.getString("role");
        methodSpecRepository.save(specs);
    }

    private void processArrayResource(String specsPath, JSONArray arrayResources, boolean fs) throws Exception {
        for (int i = 0; i < arrayResources.length(); i++) {
            JSONObject arrayResource = arrayResources.getJSONObject(i);
            String arrayName = arrayResource.getString("name");
            String arrayData = getData(specsPath + "/array/" + arrayName + ".json", fs);

            resourceModel.saveArrayResource(arrayName, arrayResource.getString("package"), new JSONArray(arrayData));
        }
    }

    private void processMapResource(String specsPath, JSONArray mapResources, boolean fs) throws Exception {
        for (int i = 0; i < mapResources.length(); i++) {
            JSONObject mapResource = mapResources.getJSONObject(i);
            String mapName = mapResource.getString("name");
            String arrayData = getData(specsPath + "/map/" + mapName + ".json", fs);
            resourceModel.saveMapResource(mapName, mapResource.getString("package"), new JSONObject(arrayData));
        }
    }

    private void processBinaryResource(String specsPath, JSONArray packageNames, boolean fs) throws Exception {
        for (int packageNameIndex = 0; packageNameIndex < packageNames.length(); packageNameIndex++) {
            String packageName = packageNames.getString(packageNameIndex);
            String specsBinary = getData(specsPath + "/binary/" + packageName + ".json", fs);
            JSONArray specs = new JSONArray(specsBinary);
            for (int i = 0; i < specs.length(); i++) {
                JSONObject jsonResource = specs.getJSONObject(i);
                String name = jsonResource.getString("name");
                String file = specsPath + "/binary/" + name;
                byte[] data = getBinaryData(file, fs);
                resourceModel.saveBinaryResource(name, packageName,
                        jsonResource.getString("contentType"), data, jsonResource.getString("role"));
            }
        }

    }

    private void processTextResource(String specsPath, JSONArray specs, boolean fs) throws Exception {
        for (int i = 0; i < specs.length(); i++) {
            String packageName = specs.getString(i);
            JSONArray textPackages = new JSONArray(
                    getData(specsPath + "/text/" + packageName + ".json", fs));
            for (int j = 0; j < textPackages.length(); j++) {
                JSONObject jsonResource = textPackages.getJSONObject(j);
                String name = jsonResource.getString("name");
                String owner = jsonResource.getString("owner");
                String title = jsonResource.getString("title");
                String file = specsPath + "/text/" + "/" + name.replace('.', '/') + ".txt";
                String data = getData(file, fs);
                resourceModel.saveTextResource(name, owner, title, packageName,
                        data, jsonResource.getString("role"));

            }
        }
    }

    private void processResource(String specsPath, JSONObject resource, boolean fs) throws Exception {
        processBinaryResource(specsPath, resource.getJSONArray("binary"), fs);
        processTextResource(specsPath, resource.getJSONArray("text"), fs);
        processArrayResource(specsPath, resource.getJSONArray("array"), fs);
        processMapResource(specsPath, resource.getJSONArray("map"), fs);
    }

    private void processComponents(String specsPath, JSONArray componentPackages, boolean fs) throws Exception {
        JSONArray missing = new JSONArray();
        HashSet<String> saved = new HashSet<>();
        for (int i = 0; i < componentPackages.length(); i++) {
            String componentPackage = componentPackages.getString(i);
            String packages = getData(specsPath + "/components/" + componentPackage + ".json", fs);
            JSONArray packageComponents = new JSONArray(packages);
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
                throw new Exception("Could not resolve all components " + missing.toString(2));
            }
        }
    }

    @jakarta.transaction.Transactional
    public void saveComponent(JSONObject component) {
        processComponent(component);
    }


    @Transactional
    public void savePage(JSONObject page) {
        Page exitingPage = pageRepository.findByMatchesId(elementHashCode(page.getString("matches")));
        if (exitingPage != null) {
            long pageId = elementHashCode(page.getString("name"));
            //CHeck if different pages
            if (pageId != exitingPage.pageId) {
                throw new RuntimeException("Page matching " + page.getString("matches") + " already exists.");
            }
        }
        processPage(page.getString("package"), page);
    }

    @Transactional
    public void renamePage(String newName, JSONObject page) {
        String originalName = page.getString("name");
        Page exitingPage = pageRepository.findByMatchesId(elementHashCode(page.getString("matches")));
        if (exitingPage != null) {
            long pageId = elementHashCode(originalName);
            //CHeck if different pages
            if (pageId != exitingPage.pageId) {
                throw new RuntimeException("Page matching " + page.getString("matches") + " already exists.");
            }
        }
        page.put("name", newName);
        processPage(page.getString("package"), page);
        exitingPage = pageRepository.findByPageId(elementHashCode(originalName));
        pageRepository.delete(exitingPage);
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

    public void processComponent(JSONObject component) {
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
