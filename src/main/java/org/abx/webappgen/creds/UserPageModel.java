package org.abx.webappgen.creds;

import org.abx.webappgen.creds.dao.PageContentRepository;
import org.abx.webappgen.creds.dao.SectionContentRepository;
import org.abx.webappgen.creds.model.Page;
import org.abx.webappgen.creds.model.PageComponent;
import org.abx.webappgen.creds.model.Component;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Component
public class UserPageModel {

    public static final String Name = "name";
    public static final String Title = "title";
    public static final String Components = "components";
    @Autowired
    public PageContentRepository  pageContentRepository;

    @Autowired
    public SectionContentRepository sectionContentRepository;

    @Transactional
    public JSONObject getPageByPageId(long id) {
        Page page = pageContentRepository.findByPageId(id);
        JSONObject jsonPage = new JSONObject();
        if (page == null){
            jsonPage.put(Title,"Not found");
            return jsonPage;
        }
        jsonPage.put(Title,page.pageTitle);
        jsonPage.put(Name,page.pageName);
        JSONArray sections = new JSONArray();
        if (page.header){
            sections.put(getComponentSpecsByComponentName("header"));
        }
        for (PageComponent pageComponent :page.pageComponents){
            sections.put(getComponentSpecsByComponent(pageComponent.component));
        }
        if (page.footer){
            sections.put(getComponentSpecsByComponentName("footer"));
        }
        jsonPage.put(Components,sections);
        return jsonPage;
    }

    public long elementHashCode(String element){
        return element.hashCode();
    }

    @Transactional
    public long createPageWithPageName(String pageName, String pageTitle,boolean header,boolean footer) {
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
    public long createComponentByComponentName(String componentName, boolean container){
        Component component = new Component();
        component.componentId = elementHashCode(componentName);
        component.componentName = componentName;
        component.container = container;
        sectionContentRepository.save(component);
        return component.componentId;
    }


    private JSONObject getComponentSpecsByComponentName(String sectionName){
        return getComponentSpecsByComponent(sectionContentRepository.findBycomponentId(elementHashCode(sectionName)));
    }

   private JSONObject getComponentSpecsByComponent(Component component){
        JSONObject jsonSection = new JSONObject();
        jsonSection.put(Name, component.componentName);
        return jsonSection;
    }
}
