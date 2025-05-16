package org.abx.webappgen.persistence;

import org.abx.webappgen.persistence.dao.*;
import org.abx.webappgen.persistence.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@org.springframework.stereotype.Component
public class PageModel {

    public static final String InnerName = "innerName";
    public static final String Name = "name";
    public static final String Title = "title";
    public static final String JS = "js";
    public static final String Component = "component";
    public static final String Layout = "layout";
    public static final String Type = "type";
    public static final String Env = "env";
    public static final String Specs = "specs";
    public static final String Components = "components";
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
    public PageComponentRepository pageComponentRepository;

    @Autowired
    public EnvValueRepository envValueRepository;

    @Transactional
    public void clean(){
        envValueRepository.deleteAll();
        innerComponentRepository.deleteAll();
        elementRepository.deleteAll();
        containerRepository.deleteAll();
        pageComponentRepository.deleteAll();
        componentRepository.deleteAll();
        pageRepository.deleteAll();
    }
    @Transactional
    public JSONObject getPageByPageId(String env, long id) {
        Page page = pageRepository.findByPageId(id);
        JSONObject jsonPage = new JSONObject();
        if (page == null) {
            jsonPage.put(Title, "Not found");
            return jsonPage;
        }
        jsonPage.put(Title, page.pageTitle);
        jsonPage.put(Name, page.pageName);
        JSONArray jsonComponents = new JSONArray();
        for (PageComponent pageComponent : page.pageComponents) {
            String targetEnv = pageComponent.env;
            if (matchesEnv(targetEnv, env)){
                jsonComponents.put(getComponentSpecsByComponent(env,pageComponent.component));
            }
        }
        jsonPage.put(Components, jsonComponents);
        jsonPage.put(Layout, "vertical");
        return jsonPage;
    }

    private boolean matchesEnv(String targetEnv, String currEnv) {
        return currEnv.contains(targetEnv);
    }

    public long elementHashCode(String element) {
        return element.hashCode();
    }

    @Transactional
    public long createPageWithPageName(String pageName, String pageTitle,JSONArray components) {
        Page page = new Page();
        page.pageName = pageName;
        page.pageId = elementHashCode(pageName);
        page.pageTitle = pageTitle;
        page.pageComponents = new ArrayList<>();
        pageRepository.save(page);
        pageRepository.flush();
        for (int i = 0; i < components.length(); i++) {
            JSONObject jsonComponent = components.getJSONObject(i);
            String innerName = jsonComponent.getString(Name);
            String componentName = jsonComponent.getString(Component);
            long componentId = elementHashCode(componentName);
            Component component = componentRepository.findBycomponentId(componentId);
            PageComponent pageComponent = new PageComponent();
            pageComponent.name = innerName;
            pageComponent.component = component;
            pageComponent.page = page;
            pageComponent.env= jsonComponent.getString(Env);
            pageComponentRepository.save(pageComponent);
            page.pageComponents.add(pageComponent);
        }
        pageRepository.save(page);
        return page.pageId;
    }


    private JSONObject getComponentSpecsByComponentName(String env, String componentName) {
        return getComponentSpecsByComponent(env,componentRepository.findBycomponentId(elementHashCode(componentName)));
    }

    private JSONObject getComponentSpecsByComponent(String env, Component component) {
        JSONObject jsonComponent = new JSONObject();
        jsonComponent.put(Name, component.componentName);
        jsonComponent.put(JS, component.js);
        boolean isContainer = component.isContainer;
        jsonComponent.put(IsContainer, isContainer);
        if (isContainer) {
            addContainer(env, jsonComponent, component);
        } else {
            addElement(jsonComponent, component);
        }
        return jsonComponent;
    }

    private void addContainer(String env, JSONObject jsonComponent, Component component) {
        Container container = component.container;
        jsonComponent.put(Layout, container.layout);
        JSONArray children = new JSONArray();
        jsonComponent.put(Components, children);
        for (InnerComponent inner : container.innerComponent) {
            JSONObject innerComponent = new JSONObject();
            children.put(innerComponent);
            innerComponent.put(InnerName, inner.name);
            innerComponent.put(Component, getComponentSpecsByComponent(env, inner.child));
        }
    }

    private void addElement(JSONObject jsonComponent, Component component) {
        Element element = component.element;
        jsonComponent.put(Specs, element.specs);
        jsonComponent.put(Type, element.type);

    }

    @Transactional
    public void createContainer(String name, JSONArray js, String layout, JSONArray children) {
        long id = elementHashCode(name);
        Component component = new Component();
        component.componentId = id;
        component.componentName = name;
        component.isContainer = true;
        componentRepository.save(component);
        Container container = new Container();
        container.component = componentRepository.findBycomponentId(id);
        container.containerId = id;
        container.layout = layout;
        container.innerComponent = new ArrayList<>();
        containerRepository.save(container);
        containerRepository.flush();
        for (int i = 0; i < children.length(); i++) {
            JSONObject jsonChild = children.getJSONObject(i);
            String childName = jsonChild.getString(Name);
            long childId = elementHashCode(childName);
            Component child = componentRepository.findBycomponentId(childId);
            InnerComponent inner = new InnerComponent();

            inner.child = child;
            inner.parent = container;
            inner.name = childName;
            innerComponentRepository.save(inner);
            container.innerComponent.add(inner);
        }
        containerRepository.save(container);

    }

    @Transactional
    public void createElement(String name, JSONArray js, String type, String specs) {
        long id = elementHashCode(name);
        Component component = new Component();
        component.componentId = id;
        component.componentName = name;
        component.isContainer = false;
        componentRepository.save(component);
        Element element = new Element();
        element.component = componentRepository.findBycomponentId(id);
        element.elementId = id;
        element.type = type;
        element.specs = specs;
        elementRepository.save(element);
        for (int i = 0; i < js.length(); i++) {
            JSONObject jsEnvValue = js.getJSONObject(i);

        }

    }
}
