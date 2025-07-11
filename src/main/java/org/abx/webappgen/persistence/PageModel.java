package org.abx.webappgen.persistence;

import org.abx.webappgen.controller.SessionEnv;
import org.abx.webappgen.persistence.dao.*;
import org.abx.webappgen.persistence.model.*;
import org.hibernate.engine.jdbc.BinaryStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.abx.webappgen.persistence.BinaryResourceModel.BinaryResources;
import static org.abx.webappgen.persistence.ResourceModel.AppEnv;
import static org.abx.webappgen.persistence.ResourceModel.AppTheme;
import static org.abx.webappgen.utils.ElementUtils.*;

@org.springframework.stereotype.Component
public class PageModel {

    public static final String InnerId = "innerId";
    public static final String Id = "id";
    public static final String Title = "title";
    public static final String JS = "js";
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
    public static final String Component = "component";
    public static final String ThemeBase = "themeBase";
    public static final String ThemeOk = "themeOk";
    public static final String ThemeText = "themeText";
    public static final String ThemeTitle = "themeTitle";
    public static final String ThemeUpdated = "themeUpdated";
    public static final String ThemeCancel = "themeCancel";
    public static final String Global = "global";

    private final long envId;
    private final long defaultEnv;
    private final long defaultThemeBase;
    private final long defaultThemeOk;
    private final long defaultThemeText;
    private final long defaultThemeUpdated;
    private final long defaultThemeCancel;
    private final long defaultThemeTitle;

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
    private ArrayPairResourceRepository arrayPairRepository;

    @Autowired
    private ArrayPairEntryRepository arrayPairEntryRepository;

    @Autowired
    private ResourceModel resourceModel;

    public PageModel() {
        envId = mapHashCode(AppEnv, "home");
        defaultEnv = mapHashCode(AppEnv, "defaultEnv");
        defaultThemeBase = mapHashCode(AppTheme, "theme.base");
        defaultThemeOk = mapHashCode(AppTheme, "theme.ok");
        defaultThemeText = mapHashCode(AppTheme, "theme.text");
        defaultThemeUpdated = mapHashCode(AppTheme, "theme.updated");
        defaultThemeCancel = mapHashCode(AppTheme, "theme.cancel");
        defaultThemeTitle = mapHashCode(AppTheme, "theme.title");
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
        arrayPairRepository.deleteAll();
        arrayPairEntryRepository.deleteAll();
    }

    @Transactional
    public String getHome() {
        return mapEntryRepository.findByMapEntryId(envId).mapValue;
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
    public void delete(String component) {
        long componentId = elementHashCode(component);
        Component toDelete = componentRepository.findByComponentId(componentId);
        List<Page> x = pageRepository.findAllByComponent(toDelete);
        if (!x.isEmpty()) {
            throw new RuntimeException("Component is used in a page");
        }
        innerComponentRepository.deleteAll(innerComponentRepository.findAllByChild(toDelete));
        innerComponentRepository.flush();
        deleteComponent(toDelete);

    }

    private void deleteComponent(Component toDelete) {
        Collection<EnvValue> jsValues = new HashSet<>(toDelete.js);
        toDelete.js.clear();
        componentRepository.save(toDelete);
        componentRepository.flush();
        envValueRepository.deleteAll(jsValues);
        envValueRepository.flush();
        if (toDelete.isContainer) {
            Container container = toDelete.container;
            Collection<InnerComponent> innerC = new HashSet<>(container.innerComponent);
            container.innerComponent.clear();
            containerRepository.save(container);
            containerRepository.flush();
            innerComponentRepository.deleteAll(innerC);
            innerComponentRepository.flush();
            containerRepository.delete(container);
            containerRepository.flush();
        } else {
            Element elem = toDelete.element;
            Collection<EnvValue> children = new HashSet<>(elem.specs);
            elem.specs.clear();
            elementRepository.save(elem);
            elementRepository.flush();
            envValueRepository.deleteAll(children);
            envValueRepository.flush();
            elementRepository.delete(elem);
            elementRepository.flush();
        }
        componentRepository.delete(toDelete);
        componentRepository.flush();
    }

    @Transactional
    public void rename(String oldName, String newName) {
        long newComponentId = elementHashCode(newName);
        Component oldCo = componentRepository.findByComponentId(elementHashCode(oldName));
        Component newCo = componentRepository.findByComponentId(newComponentId);
        List<InnerComponent> inner = innerComponentRepository.findAllByChild(oldCo);
        for (InnerComponent c : inner) {
            c.child = newCo;
            innerComponentRepository.save(c);
        }
        innerComponentRepository.flush();

        pageRepository.findAllByComponent(oldCo).forEach(p -> {
            p.component = newCo;
            pageRepository.save(p);
            pageRepository.flush();
        });

        deleteComponent(oldCo);
    }

    @Transactional
    public JSONArray getPagePackages() {
        JSONArray packages = new JSONArray();
        List<String> packageList = pageRepository.findDistinctPackageNames();
        Collections.sort(packageList);
        if (Boolean.parseBoolean(mapEntryRepository.findByMapEntryId(hideDefaultsId).mapValue)) {
            for (String p : packageList) {
                if (p.startsWith(defaultPackage)) {
                    continue;
                }
                packages.put(p);
            }
        } else {
            packages.putAll(packageList);
        }
        return packages;
    }

    @Transactional
    public JSONArray getComponentPackages() {
        JSONArray packages = new JSONArray();
        List<String> packageList = componentRepository.findDistinctPackageNames();
        Collections.sort(packageList);
        if (Boolean.parseBoolean(mapEntryRepository.findByMapEntryId(hideDefaultsId).mapValue)) {
            for (String p : packageList) {
                if (p.startsWith(defaultPackage)) {
                    continue;
                }
                packages.put(p);
            }
        } else {
            packages.putAll(packageList);
        }
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
    public JSONArray getPageNames(String packageName) {
        List<String> components = new ArrayList<>();
        for (Page page : pageRepository.findAllByPackageName(packageName)) {
            components.add(page.pageName);
        }
        Collections.sort(components);
        JSONArray componentsArray = new JSONArray();
        componentsArray.putAll(components);
        return componentsArray;
    }


    @Transactional
    public JSONObject getPageByPageId(Set<String> roles, SessionEnv env, long id) {
        Page page = pageRepository.findByPageId(id);
        return getPageByPageId(roles, env, page);
    }

    @Transactional
    public JSONObject getPageByPageMatchesId(Set<String> roles, SessionEnv env, long matchId) {
        Page page = pageRepository.findByMatchesId(matchId);
        return getPageByPageId(roles, env, page);
    }


    @Transactional
    public String defaultEnv() {
        return mapEntryRepository.findByMapEntryId(defaultEnv).mapValue;
    }

    private JSONObject getPageByPageId(Set<String> roles, SessionEnv env, Page page) {
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
        boolean found = false;
        for (EnvValue titleValue : page.pageTitle) {
            if (matchesEnv(titleValue.env, env)) {
                jsonPage.put(Title, titleValue.envValue);
                found = true;
                break;
            }
        }
        if (!found) {
            jsonPage.put(Title, page.pageTitle.stream().findFirst().get());
        }
        JSONArray scripts = new JSONArray();
        jsonPage.put(Scripts, scripts);
        for (EnvValue scriptValue : page.scripts) {
            if (matchesEnv(scriptValue.env, env)) {
                String value = scriptValue.envValue;
                if (value.startsWith(BinaryResources)) {
                    scripts.put(scriptValue.envValue);
                }
            }
        }
        JSONArray css = new JSONArray();
        jsonPage.put(CSS, css);
        for (EnvValue cssValue : page.css) {
            if (matchesEnv(cssValue.env, env)) {
                css.put(cssValue.envValue);
            }
        }
        JSONObject global = new JSONObject();
        global.put(ThemeBase, mapEntryRepository.findByMapEntryId(defaultThemeBase).mapValue);
        global.put(ThemeOk, mapEntryRepository.findByMapEntryId(defaultThemeOk).mapValue);
        global.put(ThemeCancel, mapEntryRepository.findByMapEntryId(defaultThemeCancel).mapValue);
        global.put(ThemeUpdated, mapEntryRepository.findByMapEntryId(defaultThemeUpdated).mapValue);
        global.put(ThemeText, mapEntryRepository.findByMapEntryId(defaultThemeText).mapValue);
        global.put(ThemeTitle, mapEntryRepository.findByMapEntryId(defaultThemeTitle).mapValue);
        jsonPage.put(Global, global);
        jsonPage.put(Component, processTop("top", page.component, env));
        return jsonPage;
    }


    @Transactional
    public JSONObject getComponentByName(String name, SessionEnv env) {
        return processTop("__top", componentRepository.findByComponentId(elementHashCode(name)), env);
    }

    @Transactional
    public JSONObject preview(JSONObject specs, SessionEnv env) {
        return previewComponent(specs, env);
    }

    private JSONObject processTop(String name, Component component, SessionEnv env) {
        ComponentSpecs topSpecs = new ComponentSpecs(name, env);
        topSpecs.siblings = new HashMap<>();
        topSpecs.siblings.put("self", name);
        topSpecs.component = component;
        JSONObject componentSpecs = getComponentSpecsByComponent(topSpecs);
        componentSpecs.put(Id, name);
        componentSpecs.put(Size, "");
        return componentSpecs;
    }

    private JSONObject processTop(JSONObject previewComponentSpecs, SessionEnv env) {
        JSONObject componentSpecs = previewComponent(previewComponentSpecs, env);
        componentSpecs.put(Size, "");
        return componentSpecs;
    }

    private boolean matchesEnv(String currEnv, SessionEnv targetEnv) {
        return targetEnv.matches(currEnv);
    }


    @Transactional
    public long createPageWithPageName(String pageName, String packageName, String matches, JSONArray pageTitle, String role, String componentName, JSONArray css, JSONArray scripts) {
        Page page = new Page();
        page.pageName = pageName;
        page.packageName = packageName;
        page.pageId = elementHashCode(pageName);
        page.role = role;
        page.matches = matches;
        page.matchesId = elementHashCode(matches);
        page.pageTitle = createEnvValues(pageTitle);
        page.css = createEnvValues(css);
        page.scripts = createEnvValues(scripts);
        pageRepository.save(page);
        pageRepository.flush();
        page.component = componentRepository.findByComponentId(elementHashCode(componentName));
        pageRepository.save(page);
        return page.pageId;
    }


    private String addTempVariables(String source, Map<String, String> replacements) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            sb.append("let ").append(entry.getKey()).append(" = ").append(entry.getValue()).append(";\n");

        }
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            sb.append("self.").append(entry.getKey()).append(" = ").append(entry.getKey()).append(";\n");

        }
        return "{\n" + sb + source + "}";
    }

    private JSONObject previewComponent(JSONObject jsonComponent, SessionEnv env) {
        boolean isContainer = jsonComponent.getBoolean("isContainer");
        jsonComponent.put(Id, "__preview");
        if (isContainer) {
            addPreviewContainer(jsonComponent, env);
            jsonComponent.put(JS, "");
        } else {
            boolean found = false;
            JSONArray specs = jsonComponent.getJSONArray(Specs);
            for (int i = 0; i < specs.length(); i++) {
                JSONObject spec = specs.getJSONObject(i);
                if (matchesEnv(spec.getString("env"), env)) {
                    jsonComponent.put(Specs, spec.getJSONObject("value"));
                    found = true;
                    break;
                }
            }
            //In case of desperation use first one
            if (!found) {
                jsonComponent.put(Specs, specs.getJSONObject(0).getString("value"));
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
                sb.append(jsValue.envValue).append("\n");
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

    private void addPreviewContainer(JSONObject jsonComponent, SessionEnv env) {
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
            Component child = componentRepository.findByComponentId(elementHashCode(childName));
            ComponentSpecs componentSpecs = new ComponentSpecs("", env);
            Map<String, String> childrenInnerIds = new HashMap<>();
            componentSpecs.component = child;
            componentSpecs.siblings = childrenInnerIds;
            JSONObject innerComponent = getComponentSpecsByComponent(componentSpecs);
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
        }
        for (InnerComponent inner : container.innerComponent) {
            String innerId = specs.parent + "_" + inner.innerId;
            ComponentSpecs componentSpecs = specs.child(innerId);
            componentSpecs.siblings = childrenInnerIds;
            componentSpecs.component = inner.child;
            componentSpecs.siblings.put("self", innerId);
            JSONObject innerComponent = getComponentSpecsByComponent(componentSpecs);
            jsonChildren.put(innerComponent);
            innerComponent.put(Id, innerId);
            innerComponent.put(Size, inner.size);
        }
        return childrenInnerIds;
    }

    private JSONObject reviewSrc(JSONObject jsonObject) {
        if (jsonObject.has("src")) {
            jsonObject.put("src", resourceModel.cacheResource(jsonObject.getString("src")));
        }
        return jsonObject;
    }

    private void addElement(ComponentSpecs specs, JSONObject jsonComponent) {
        Element element = specs.component.element;
        boolean found = false;
        String emptyEnv = null;
        String first = null;
        for (EnvValue envValue : element.specs) {
            if (first == null) {
                first = envValue.envValue;
            }
            if (envValue.env.isEmpty()) {
                emptyEnv = envValue.envValue;
            } else {
                if (matchesEnv(envValue.env, specs.env)) {
                    jsonComponent.put(Specs, reviewSrc(new JSONObject(envValue.envValue)));
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            if (emptyEnv == null) {
                jsonComponent.put(Specs, reviewSrc(new JSONObject(first)));
            } else {
                jsonComponent.put(Specs, reviewSrc(new JSONObject(emptyEnv)));
            }
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

    public ArrayList<EnvValue> createEnvValues(JSONArray envValues) {
        ArrayList<EnvValue> env = new ArrayList<>();
        for (int i = 0; i < envValues.length(); i++) {
            JSONObject jsEnvValue = envValues.getJSONObject(i);
            env.add(createEnvValue(jsEnvValue));
        }
        return env;
    }

    public EnvValue createEnvValue(JSONObject jsonEnvValue) {
        EnvValue envValue = new EnvValue();
        envValue.env = jsonEnvValue.getString("env");
        envValue.envValue = jsonEnvValue.get("value").toString();
        envValueRepository.save(envValue);
        return envValue;
    }

    public void saveComponent(JSONArray js, Component component) {
        componentRepository.save(component);
        componentRepository.flush();
        component.js = createEnvValues(js);
        componentRepository.save(component);
    }

}
