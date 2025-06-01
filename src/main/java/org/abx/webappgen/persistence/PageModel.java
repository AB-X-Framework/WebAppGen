package org.abx.webappgen.persistence;

import org.abx.webappgen.persistence.dao.*;
import org.abx.webappgen.persistence.model.*;
import org.abx.webappgen.utils.SpecsExporter;
import org.abx.webappgen.utils.SpecsImporter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@org.springframework.stereotype.Component
public class PageModel {

    public static final String InnerId = "innerId";
    public static final String Id = "id";
    public static final String Title = "title";
    public static final String JS = "js";
    public static final String Component = "component";
    ;
    public static final String Components = "components";
    public static final String Layout = "layout";
    public static final String Type = "type";
    public static final String Size = "size";
    public static final String CSS = "css";
    public static final String Scripts = "scripts";
    public static final String Env = "env";
    public static final String Specs = "specs";
    public static final String Children = "children";
    public static final String Package = "package";
    public static final String IsContainer = "isContainer";

    long envId;

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
    private UserRepository userRepository;
    @Autowired
    private SpecsExporter specsExporter;
    @Autowired
    private SpecsImporter specsImporter;

    public PageModel() {
        envId = PageModel.mapHashCode("org.abx.app.Env", "home");
    }

    @Transactional
    public void clean() {
        userRepository.deleteAll();
        methodSpecRepository.deleteAll();
        mapEntryRepository.deleteAll();
        mapResourceRepository.deleteAll();
        arrayEntryRepository.deleteAll();
        arrayResourceRepository.deleteAll();
        textResourceRepository.deleteAll();
        binaryResourceRepository.deleteAll();
        envValueRepository.deleteAll();
        innerComponentRepository.deleteAll();
        elementRepository.deleteAll();
        containerRepository.deleteAll();
        componentRepository.deleteAll();
        pageRepository.deleteAll();
    }

    @Transactional
    public String getHome() {
        return mapEntryRepository.findByMapEntryId(envId).value;
    }

    @Transactional
    public boolean validPage(Set<String> roles, long matchesId) {
        Page page = pageRepository.findByMatchesId(matchesId);
        if (page == null) {
            return false;
        }
        if (!"Anonymous".equals(page.role)) {
            return roles.contains(page.role);
        }
        return true;
    }

    @Transactional
    public JSONArray getPackages() {
        JSONArray packages = new JSONArray();
        List<String> packageList = componentRepository.findDistinctPackageNames();
        Collections.sort(packageList);
        packages.putAll(packageList);
        return packages;
    }

    @Transactional
    public JSONArray getComponentNames(String packageName) {
        List<String> components = new ArrayList<>();
        for (Component component : componentRepository.findAllByPackageName(packageName)) {
            components.add(component.componentName);
        }
        Collections.sort(components);
        JSONArray componentsArray = new JSONArray();
        componentsArray.putAll(components);
        return componentsArray;
    }


    @Transactional
    public JSONObject getPageByPageId(Set<String> roles, String env, long id) {
        Page page = pageRepository.findByPageId(id);
        return getPageByPageId(roles, env, page);
    }

    @Transactional
    public JSONObject getPageByPageMatchesId(Set<String> roles, String env, long matchId) {
        Page page = pageRepository.findByMatchesId(matchId);
        return getPageByPageId(roles, env, page);
    }

    private JSONObject getPageByPageId(Set<String> roles, String env, Page page) {

        JSONObject jsonPage = new JSONObject();
        if (page == null) {
            jsonPage.put(Title, "Not found");
            return jsonPage;
        }
        if (!"Anonymous".equals(page.role)) {
            if (!roles.contains(page.role)) {
                jsonPage.put(Title, "Not authorized");
                return jsonPage;
            }
        }

        jsonPage.put(Title, page.pageTitle);
        JSONArray scripts = new JSONArray();
        jsonPage.put(Scripts, scripts);
        for (EnvValue scriptValue : page.scripts) {
            if (matchesEnv(scriptValue.env, env)) {
                scripts.put(scriptValue.value);
            }
        }
        JSONArray css = new JSONArray();
        jsonPage.put(CSS, css);
        for (EnvValue cssValue : page.css) {
            if (matchesEnv(cssValue.env, env)) {
                css.put(cssValue.value);
            }
        }
        jsonPage.put(Component, processTop("top", page.component, env));
        return jsonPage;
    }


    @Transactional
    public JSONObject getComponentByName(String name, String env) {
        return processTop("__top", componentRepository.findByComponentId(elementHashCode(name)), env);
    }

    @Transactional
    public JSONObject preview(JSONObject specs, String env) {
        return previewComponent(specs, env);
    }

    @Transactional
    public void cloneComponent(JSONObject specs, String newName) {
        specs.put("name", newName);
        specs.put("package",newName.substring(0, newName.lastIndexOf(".")));
        specsImporter.processComponent(specs);

    }


    private JSONObject processTop(String name, Component component, String env) {
        ComponentSpecs topSpecs = new ComponentSpecs(name, env);
        topSpecs.siblings = new HashMap<>();
        topSpecs.siblings.put("self", name);
        topSpecs.component = component;
        JSONObject componentSpecs = getComponentSpecsByComponent(topSpecs);
        componentSpecs.put(Id, name);
        componentSpecs.put(Size, "");
        return componentSpecs;
    }

    private JSONObject processTop(JSONObject previewComponentSpecs, String env) {
        JSONObject componentSpecs = previewComponent(previewComponentSpecs, env);
        componentSpecs.put(Size, "");
        return componentSpecs;
    }

    private boolean matchesEnv(String targetEnv, String currEnv) {
        return currEnv.contains(targetEnv);
    }

    public static long elementHashCode(String element) {
        return element.hashCode();
    }

    public static long mapHashCode(String map, String key) {
        return elementHashCode(map + "." + key);
    }

    @Transactional
    public long createPageWithPageName(String pageName, String packageName,
                                       String matches,
                                       String pageTitle,
                                       String role, String componentName,
                                       JSONArray css, JSONArray scripts) {
        Page page = new Page();
        page.pageName = pageName;
        page.packageName = packageName;
        page.pageId = elementHashCode(pageName);
        page.role = role;
        page.matches = matches;
        page.matchesId = elementHashCode(matches);
        page.pageTitle = pageTitle;
        page.css = createEnvValues(css);
        page.scripts = createEnvValues(scripts);
        pageRepository.save(page);
        pageRepository.flush();
        page.component = componentRepository.findByComponentId(elementHashCode(componentName));
        pageRepository.save(page);
        return page.pageId;
    }


    private String addTempVariables(String source, Map<String, String> replacements) {
        if (source.isBlank() || replacements.isEmpty()) {
            return source;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            if (source.contains(entry.getKey())) {
                sb.append("let ").append(entry.getKey()).append(" = ").append(entry.getValue()).append(";\n");
            }
        }
        return "{\n" + sb + source + "}";
    }

    private JSONObject previewComponent(JSONObject jsonComponent, String env) {
        boolean isContainer = jsonComponent.getBoolean("isContainer");
        jsonComponent.put(Id, "__preview");
        if (isContainer) {
            addPreviewContainer(jsonComponent, env);
            jsonComponent.put(JS, "");
        } else {
            JSONArray specs = jsonComponent.getJSONArray(Specs);
            for (int i = 0; i < specs.length(); i++) {
                JSONObject spec = specs.getJSONObject(i);
                if (matchesEnv(spec.getString("env"), env)) {
                    jsonComponent.put(Specs, spec.getJSONObject("value"));
                    break;
                }
            }
            jsonComponent.put(JS, "");
        }
        return jsonComponent;
    }

    private JSONObject getComponentSpecsByComponent(ComponentSpecs specs) {
        Component component = specs.component;
        JSONObject jsonComponent = new JSONObject();
        StringBuilder sb = new StringBuilder();
        for (EnvValue jsValue : component.js) {
            if (matchesEnv(jsValue.env, specs.env)) {
                sb.append(jsValue.value).append("\n");
            }
        }
        boolean isContainer = component.isContainer;
        jsonComponent.put(IsContainer, isContainer);
        jsonComponent.put(Package, component.packageName);

        if (isContainer) {
            Map<String, String> children = addContainer(specs, jsonComponent);
            children.putAll(specs.siblings);
            jsonComponent.put(JS, addTempVariables(sb.toString(), children));
        } else {
            jsonComponent.put(JS, addTempVariables(sb.toString(), specs.siblings));
            addElement(specs, jsonComponent);
        }
        return jsonComponent;
    }

    private void addPreviewContainer(JSONObject jsonComponent, String env) {
        JSONArray jsonChildren = new JSONArray();
        jsonComponent.put(Children, jsonChildren);
        JSONArray jsonChildrenComponents = jsonComponent.getJSONArray(Components);
        for (int i = 0; i < jsonChildrenComponents.length(); i++) {
            JSONObject childComponent = jsonChildrenComponents.getJSONObject(i);
            String childEnv = childComponent.getString("env");
            if (!matchesEnv(childEnv, env)) {
                continue;
            }
            String childName = childComponent.getString(Component);
            org.abx.webappgen.persistence.model.Component child =
                    componentRepository.findByComponentId(elementHashCode(childName));
            ComponentSpecs componentSpecs = new ComponentSpecs("", env);
            Map<String, String> childrenInnerIds = new HashMap<>();
            componentSpecs.component = child;
            componentSpecs.siblings = childrenInnerIds;
            JSONObject innerComponent =
                    getComponentSpecsByComponent(componentSpecs);
            jsonChildren.put(innerComponent);
            innerComponent.put(Size, childComponent.getString(Size));
        }
    }

    private Map<String, String> addContainer(ComponentSpecs specs, JSONObject jsonComponent) {
        Component component = specs.component;
        Container container = component.container;
        jsonComponent.put(Layout, container.layout);
        JSONArray jsonChildren = new JSONArray();
        jsonComponent.put(Children, jsonChildren);
        Map<String, String> childrenInnerIds = new HashMap<>();
        for (InnerComponent inner : container.innerComponent) {
            childrenInnerIds.put(inner.innerId, specs.parent + "_" + inner.innerId);
            childrenInnerIds.put(inner.child.componentName, specs.parent + "_" + inner.innerId);
        }
        for (InnerComponent inner : container.innerComponent) {
            String innerId = specs.parent + "_" + inner.innerId;
            ComponentSpecs componentSpecs = specs.child(innerId);
            componentSpecs.siblings = childrenInnerIds;
            componentSpecs.component = inner.child;
            componentSpecs.siblings.put("self", innerId);
            JSONObject innerComponent =
                    getComponentSpecsByComponent(componentSpecs);
            jsonChildren.put(innerComponent);
            innerComponent.put(Id, innerId);
            innerComponent.put(Size, inner.size);
        }
        return childrenInnerIds;
    }


    private void addElement(ComponentSpecs specs, JSONObject jsonComponent) {
        Element element = specs.component.element;
        boolean found = false;
        String emptyEnv = null;
        for (EnvValue envValue : element.specs) {
            if (matchesEnv(envValue.env, specs.env)) {
                if (specs.env.isEmpty()) {
                    emptyEnv = envValue.value;
                } else {
                    jsonComponent.put(Specs, new JSONObject(envValue.value));
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            jsonComponent.put(Specs, new JSONObject(emptyEnv));
        }
        jsonComponent.put(Type, element.type);

    }

    @Transactional
    public void createContainer(String name, String packageName, JSONArray js, String layout, JSONArray children) {
        long id = elementHashCode(name);
        Component component = new Component();
        component.componentId = id;
        component.packageName = packageName;
        component.componentName = name;
        component.isContainer = true;
        saveComponent(js, component);
        Container container = new Container();
        container.component = componentRepository.findByComponentId(id);
        container.containerId = id;
        container.layout = layout;
        container.innerComponent = new ArrayList<>();
        containerRepository.save(container);
        containerRepository.flush();
        for (int i = 0; i < children.length(); i++) {
            JSONObject jsonChild = children.getJSONObject(i);
            String childComponent = jsonChild.getString(Component);
            long childId = elementHashCode(childComponent);
            Component child = componentRepository.findByComponentId(childId);
            InnerComponent inner = new InnerComponent();

            inner.child = child;
            inner.parent = container;
            inner.innerId = jsonChild.getString(InnerId);
            inner.size = jsonChild.getString(Size);
            inner.env = jsonChild.getString(Env);
            innerComponentRepository.save(inner);
            container.innerComponent.add(inner);
        }
        containerRepository.save(container);
    }

    private void saveComponent(JSONArray js, Component component) {
        componentRepository.save(component);
        componentRepository.flush();
        component.js = createEnvValues(js);
        componentRepository.save(component);
    }

    private ArrayList<EnvValue> createEnvValues(JSONArray envValues) {
        ArrayList<EnvValue> env = new ArrayList<>();
        for (int i = 0; i < envValues.length(); i++) {
            JSONObject jsEnvValue = envValues.getJSONObject(i);
            env.add(createEnvValue(jsEnvValue));
        }
        return env;
    }

    @Transactional
    public void createElement(String name, String packageName, JSONArray js, String type, JSONArray specs) {
        long id = elementHashCode(name);
        Component component = new Component();
        component.packageName = packageName;
        component.componentId = id;
        component.componentName = name;
        component.isContainer = false;
        saveComponent(js, component);
        Element element = new Element();
        element.component = componentRepository.findByComponentId(id);
        element.elementId = id;
        element.type = type;
        elementRepository.save(element);
        elementRepository.flush();
        element.specs = new ArrayList<>();
        for (int i = 0; i < specs.length(); i++) {
            JSONObject specsEnvValue = specs.getJSONObject(i);
            element.specs.add(createEnvValue(specsEnvValue));
        }
        elementRepository.save(element);

    }

    private EnvValue createEnvValue(JSONObject jsonEnvValue) {
        EnvValue envValue = new EnvValue();
        envValue.env = jsonEnvValue.getString("env");
        envValue.value = jsonEnvValue.get("value").toString();
        envValueRepository.save(envValue);
        return envValue;
    }
}
