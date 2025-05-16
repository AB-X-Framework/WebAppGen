package org.abx.webappgen.persistence;

import org.abx.webappgen.persistence.dao.*;
import org.abx.webappgen.persistence.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@org.springframework.stereotype.Component
public class UserPageModel {

    public static final String Name = "name";
    public static final String Title = "title";
    public static final String JS = "js";
    public static final String Component = "component";
    public static final String Layout = "layout";
    public static final String Type = "type";
    public static final String Specs = "specs";
    public static final String Components = "components";
    public static final String IsContainer = "isContainer";
    @Autowired
    public PageContentRepository pageContentRepository;

    @Autowired
    public ComponentRepository componentRepository;

    @Autowired
    public ContainerRepository containerRepository;

    @Autowired
    public ElementRepository specsComponentRepository;

    @Autowired
    public InnerComponentRepository innerComponentRepository;

    @Transactional
    public JSONObject getPageByPageId(long id) {
        Page page = pageContentRepository.findByPageId(id);
        JSONObject jsonPage = new JSONObject();
        if (page == null) {
            jsonPage.put(Title, "Not found");
            return jsonPage;
        }
        jsonPage.put(Title, page.pageTitle);
        jsonPage.put(Name, page.pageName);
        JSONArray jsonComponents = new JSONArray();
        if (page.header) {
            jsonComponents.put(getComponentSpecsByComponentName("header"));
        }
        for (PageComponent pageComponent : page.pageComponents) {
            jsonComponents.put(getComponentSpecsByComponent(pageComponent.component));
        }
        if (page.footer) {
            jsonComponents.put(getComponentSpecsByComponentName("footer"));
        }
        jsonPage.put(Components, jsonComponents);
        jsonPage.put(Layout, "vertical");
        return jsonPage;
    }

    public long elementHashCode(String element) {
        return element.hashCode();
    }

    @Transactional
    public long createPageWithPageName(String pageName, String pageTitle, boolean header, boolean footer) {
        Page page = new Page();
        page.pageName = pageName;
        page.pageId = elementHashCode(pageName);
        page.pageTitle = pageTitle;
        page.header = header;
        page.footer = footer;
        pageContentRepository.save(page);
        return page.pageId;
    }


    private JSONObject getComponentSpecsByComponentName(String componentName) {
        return getComponentSpecsByComponent(componentRepository.findBycomponentId(elementHashCode(componentName)));
    }

    private JSONObject getComponentSpecsByComponent(Component component) {
        JSONObject jsonComponent = new JSONObject();
        jsonComponent.put(Name, component.componentName);
        jsonComponent.put(JS, component.js);
        boolean isContainer = component.isContainer;
        jsonComponent.put(IsContainer, isContainer);
        if (isContainer) {
            addContainer(jsonComponent, component);
        } else {
            addElement(jsonComponent, component);
        }
        return jsonComponent;
    }

    private void addContainer(JSONObject jsonComponent, Component component) {
        Container container = component.container;
        jsonComponent.put(Layout, container.layout);
        JSONArray children = new JSONArray();
        jsonComponent.put(Components, children);
        for (InnerComponent inner : container.innerComponent) {
            JSONObject innerComponent = new JSONObject();
            children.put(innerComponent);
            innerComponent.put(Name, inner.name);
            innerComponent.put(Component, getComponentSpecsByComponent(inner.child));
        }
    }

    private void addElement(JSONObject jsonComponent, Component component) {
        Element element = component.element;
        jsonComponent.put(Specs, element.specs);
        jsonComponent.put(Specs, element.type);

    }

    @Transactional
    public void createContainer(String name, String js, String layout, JSONArray children) {
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
    public void createElement(String name, String js, String type, String specs) {
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
        specsComponentRepository.save(element);

    }
}
