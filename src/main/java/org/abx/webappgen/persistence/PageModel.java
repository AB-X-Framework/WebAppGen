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
    public static final String Name = "name";
    public static final String Title = "title";
    public static final String JS = "js";
    public static final String Component = "component";
    public static final String Layout = "layout";
    public static final String Type = "type";
    public static final String Env = "env";
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
        jsonPage.put(Component, getComponentSpecsByComponent("top", new HashMap<>(), env, page.component));
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


    private JSONObject getComponentSpecsByComponentName(String parent, Map<String, String> siblings, String env, String componentName) {
        return getComponentSpecsByComponent(parent, siblings, env,
                componentRepository.findBycomponentId(elementHashCode(componentName)));
    }

    private String replaceWholeWords(String source, Map<String, String> replacements) {
        String result = source;

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String target = entry.getKey();
            String replacement = entry.getValue();

            // Match the whole word using word boundaries
            String regex = "\\b" + Pattern.quote(target) + "\\b";
            result = result.replaceAll(regex, Matcher.quoteReplacement(replacement));
        }

        return result;
    }

    private JSONObject getComponentSpecsByComponent(String parent, Map<String, String> siblings,
                                                    String env, Component component) {
        JSONObject jsonComponent = new JSONObject();
        StringBuilder sb = new StringBuilder();
        for (EnvValue jsValue : component.js) {
            if (matchesEnv(jsValue.env, env)) {
                sb.append(replaceWholeWords(jsValue.value, siblings));
            }
        }
        jsonComponent.put(JS, sb.toString());
        boolean isContainer = component.isContainer;
        jsonComponent.put(IsContainer, isContainer);
        if (isContainer) {
            addContainer(parent, env, jsonComponent, component);
        } else {
            addElement(env, jsonComponent, component);
        }
        return jsonComponent;
    }

    private void addContainer(String parent, String env, JSONObject jsonComponent, Component component) {
        Container container = component.container;
        jsonComponent.put(Layout, container.layout);
        JSONArray children = new JSONArray();
        jsonComponent.put(Children, children);
        Map<String,String> siblings = new HashMap<>();
        for (InnerComponent inner : container.innerComponent) {
            siblings.put(inner.innerId, parent + "_" + inner.innerId);
        }
        for (InnerComponent inner : container.innerComponent) {
            String innerId = parent + "_" + inner.innerId;
            JSONObject innerComponent = getComponentSpecsByComponent(innerId, siblings, env, inner.child);
            children.put(innerComponent);
            innerComponent.put(Id, innerId);
            innerComponent.put(Size, inner.size);
        }
    }

    private void addElement(String env, JSONObject jsonComponent, Component component) {
        Element element = component.element;
        for (EnvValue envValue : element.specs) {
            if (matchesEnv(envValue.env, env)) {
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
            inner.size = jsonChild.getInt("size");
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
