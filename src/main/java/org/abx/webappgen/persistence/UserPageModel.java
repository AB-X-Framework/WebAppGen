package org.abx.webappgen.persistence;

import org.abx.webappgen.persistence.dao.ContainerRepository;
import org.abx.webappgen.persistence.dao.PageContentRepository;
import org.abx.webappgen.persistence.dao.ComponentRepository;
import org.abx.webappgen.persistence.dao.ElementRepository;
import org.abx.webappgen.persistence.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Component
public class UserPageModel {

    public static final String Name = "name";
    public static final String Title = "title";
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
        JSONArray sections = new JSONArray();
        if (page.header) {
            sections.put(getComponentSpecsByComponentName("header"));
        }
        for (PageComponent pageComponent : page.pageComponents) {
            sections.put(getComponentSpecsByComponent(pageComponent.component));
        }
        if (page.footer) {
            sections.put(getComponentSpecsByComponentName("footer"));
        }
        jsonPage.put(Components, sections);
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


    private JSONObject getComponentSpecsByComponentName(String sectionName) {
        return getComponentSpecsByComponent(componentRepository.findBycomponentId(elementHashCode(sectionName)));
    }

    private JSONObject getComponentSpecsByComponent(Component component) {
        JSONObject jsonSection = new JSONObject();
        jsonSection.put(Name, component.componentName);
        jsonSection.put(IsContainer, component.container);
        return jsonSection;
    }

    @Transactional
    public void createContainer(String name, String js,
                                String layout, JSONArray children) {
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
        containerRepository.save(container);

    }

    @Transactional
    public void createElement(String name,  String js,
                              String type, String specs) {
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
