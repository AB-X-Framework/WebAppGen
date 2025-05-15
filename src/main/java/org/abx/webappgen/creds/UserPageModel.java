package org.abx.webappgen.creds;

import org.abx.webappgen.creds.dao.PageContentRepository;
import org.abx.webappgen.creds.model.Page;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserPageModel {

    public static final String Title = "title";
    public static final String Sections = "sections";
    @Autowired
    public PageContentRepository  pageContentRepository;

    @Transactional
    public JSONObject getPageByPageId(long id) {
        Page page = pageContentRepository.findByPageId(id);
        JSONObject jsonPage = new JSONObject();
        JSONArray sections = new JSONArray();
        if (page == null){
            jsonPage.put(Title,"Not found");
            return jsonPage;
        }
        jsonPage.put(Title,page.pageId);
        return jsonPage;
    }

    public long pageHashCode(String pagename){
        return pagename.hashCode();
    }

    @Transactional
    public long createPageWithPagename(String pagename,String pageTitle) {
        Page page = new Page();
        page.pagename = pagename;
        page.pageId = pageHashCode(pagename);
        page.pageTitle = pageTitle;
        pageContentRepository.save(page);
        return page.pageId;
    }
}
