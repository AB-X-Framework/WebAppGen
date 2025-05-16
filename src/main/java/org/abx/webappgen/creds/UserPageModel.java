package org.abx.webappgen.creds;

import org.abx.webappgen.creds.dao.ContainerRepository;
import org.abx.webappgen.creds.dao.PageContentRepository;
import org.abx.webappgen.creds.dao.ComponentRepository;
import org.abx.webappgen.creds.model.Component;
import org.abx.webappgen.creds.model.Container;
import org.abx.webappgen.creds.model.Page;
import org.abx.webappgen.creds.model.PageComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Component
public class UserPageModel {

    public static final String Name = "name";
    public static final String Title = "title";
    public static final String Components = "components";
    public static final String Container = "container";
    @Autowired
    public PageContentRepository pageContentRepository;

    @Autowired
    public ComponentRepository componentRepository;

    @Autowired
    public ContainerRepository containerRepository;
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


    @Transactional
    public long createComponentByComponentName(String componentName, boolean isContainer) {
        Component component = new Component();
        component.componentId = elementHashCode(componentName);
        component.componentName = componentName;
        component.isContainer = isContainer;
        componentRepository.save(component);
        return component.componentId;
    }


    private JSONObject getComponentSpecsByComponentName(String sectionName) {
        return getComponentSpecsByComponent(componentRepository.findBycomponentId(elementHashCode(sectionName)));
    }

    private JSONObject getComponentSpecsByComponent(Component component) {
        JSONObject jsonSection = new JSONObject();
        jsonSection.put(Name, component.componentName);
        jsonSection.put(Container, component.container);
        return jsonSection;
    }

    @Transactional
    public void createContainer(long id, String layout, JSONArray children) {
        Container container = new Container();
        container.component = componentRepository.findBycomponentId(id);
        container.containerId = id;
        container.layout = layout;
        containerRepository.save(container);

    }
}
