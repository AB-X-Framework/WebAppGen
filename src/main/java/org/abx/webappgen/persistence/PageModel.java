package org.abx.webappgen.persistence;

import org.abx.webappgen.persistence.dao.*;
import org.abx.webappgen.persistence.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Component
public class PageModel {

    public static final String InnerId = "innerId";
    public static final String Id = "id";
    public static final String Title = "title";
    public static final String JS = "js";
    public static final String Component = "component";
    public static final String Layout = "layout";
    public static final String Type = "type";
    public static final String Size = "size";
    public static final String Specs = "specs";
    public static final String Children = "children";
    public static final String IsContainer = "isContainer";
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
    public ResourceModel resourceModel;

    @Transactional
    public void clean() {
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
    public JSONObject getPageByPageId(Set<String> roles, String env, long id) {
        Page page = pageRepository.findByPageId(id);
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
        String top = "top";
        ComponentSpecs topSpecs = new ComponentSpecs(top,env);
        topSpecs.siblings = new HashMap<>();
        topSpecs.siblings.put("self",top);
        topSpecs.component = page.component;
        jsonPage.put(Component, getComponentSpecsByComponent(topSpecs));
        return jsonPage;
    }

    private boolean matchesEnv(String targetEnv, String currEnv) {
        return currEnv.contains(targetEnv);
    }

    public static long elementHashCode(String element) {
        return element.hashCode();
    }

    @Transactional
    public long createPageWithPageName(String pageName, String pageTitle,
                                       String role, String componentName) {
        Page page = new Page();
        page.pageName = pageName;
        page.pageId = elementHashCode(pageName);
        page.role = role;
        page.pageTitle = pageTitle;
        pageRepository.save(page);
        pageRepository.flush();
        page.component = componentRepository.findBycomponentId(elementHashCode(componentName));
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

        return "{\n"+ sb +source+"}";
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
        if (isContainer) {
            Map<String,String> children = addContainer(specs, jsonComponent);
            children.putAll(specs.siblings);
            jsonComponent.put(JS,addTempVariables(sb.toString(),children));
        } else {
            jsonComponent.put(JS, addTempVariables(sb.toString(),specs.siblings));
            addElement(specs, jsonComponent);
        }
        return jsonComponent;
    }

    private  Map<String,String> addContainer(ComponentSpecs specs,  JSONObject jsonComponent) {
        Component component = specs.component;
        Container container = component.container;
        jsonComponent.put(Layout, container.layout);
        JSONArray jsonChildren = new JSONArray();
        jsonComponent.put(Children, jsonChildren);
        Map<String,String> childrenInnerIds = new HashMap<>();
        for (InnerComponent inner : container.innerComponent) {
            childrenInnerIds.put(inner.innerId, specs.parent + "_" + inner.innerId);
            childrenInnerIds.put(inner.child.componentName, specs.parent + "_" + inner.innerId);
        }
        for (InnerComponent inner : container.innerComponent) {
            String innerId = specs.parent + "_" + inner.innerId;
            ComponentSpecs componentSpecs = specs.child(innerId);
            componentSpecs.siblings = childrenInnerIds;
            componentSpecs.component = inner.child;
            componentSpecs.siblings.put("self",innerId);
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
        for (EnvValue envValue : element.specs) {
            if (matchesEnv(envValue.env, specs.env)) {
                jsonComponent.put(Specs, new JSONObject(envValue.value));
                break;
            }
        }
        jsonComponent.put(Type, element.type);

    }

    @Transactional
    public void createContainer(String name, JSONArray js, String layout, JSONArray children) {
        long id = elementHashCode(name);
        Component component = new Component();
        component.componentId = id;
        component.componentName = name;
        component.isContainer = true;
        saveComponent(js, component);
        Container container = new Container();
        container.component = componentRepository.findBycomponentId(id);
        container.containerId = id;
        container.layout = layout;
        container.innerComponent = new ArrayList<>();
        containerRepository.save(container);
        containerRepository.flush();
        for (int i = 0; i < children.length(); i++) {
            JSONObject jsonChild = children.getJSONObject(i);
            String childComponent = jsonChild.getString(Component);
            long childId = elementHashCode(childComponent);
            Component child = componentRepository.findBycomponentId(childId);
            InnerComponent inner = new InnerComponent();

            inner.child = child;
            inner.parent = container;
            inner.innerId = jsonChild.getString(InnerId);
            inner.size = jsonChild.getString("size");
            innerComponentRepository.save(inner);
            container.innerComponent.add(inner);
        }
        containerRepository.save(container);

    }

    private void saveComponent(JSONArray js, Component component) {
        componentRepository.save(component);
        componentRepository.flush();
        component.js = new ArrayList<>();
        for (int i = 0; i < js.length(); i++) {
            JSONObject jsEnvValue = js.getJSONObject(i);
            component.js.add(createEnvValue(jsEnvValue));
        }
        componentRepository.save(component);
    }

    @Transactional
    public void createElement(String name, JSONArray js, String type, JSONArray specs) {
        long id = elementHashCode(name);
        Component component = new Component();
        component.componentId = id;
        component.componentName = name;
        component.isContainer = false;
        saveComponent(js, component);
        Element element = new Element();
        element.component = componentRepository.findBycomponentId(id);
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
