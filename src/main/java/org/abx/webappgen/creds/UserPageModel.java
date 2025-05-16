package org.abx.webappgen.creds;

import org.abx.webappgen.creds.dao.PageContentRepository;
import org.abx.webappgen.creds.dao.SectionContentRepository;
import org.abx.webappgen.creds.model.Page;
import org.abx.webappgen.creds.model.PageSection;
import org.abx.webappgen.creds.model.Section;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserPageModel {

    public static final String Name = "name";
    public static final String Title = "title";
    public static final String Sections = "sections";
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
        for (PageSection pageSection:page.pageSections){
            sections.put(getSectionSpecsBySection(pageSection.section));
        }
        jsonPage.put(Sections,sections);
        return jsonPage;
    }

    public long elementHashCode(String element){
        return element.hashCode();
    }

    @Transactional
    public long createPageWithPageName(String pageName, String pageTitle) {
        Page page = new Page();
        page.pageName = pageName;
        page.pageId = elementHashCode(pageName);
        page.pageTitle = pageTitle;
        pageContentRepository.save(page);
        return page.pageId;
    }


    @Transactional
    public long createSectionBySectionName(String sectionName){
        Section section = new Section();
        section.sectionId = elementHashCode(sectionName);
        section.sectionName = sectionName;
        sectionContentRepository.save(section);
        return section.sectionId;
    }

   private JSONObject getSectionSpecsBySection(Section section){
        JSONObject jsonSection = new JSONObject();
        jsonSection.put(Name,section.sectionName);
        return jsonSection;
    }
}
