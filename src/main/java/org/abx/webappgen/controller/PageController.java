package org.abx.webappgen.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.dao.BinaryResourceRepository;
import org.abx.webappgen.persistence.dao.MapEntryRepository;
import org.abx.webappgen.utils.SpecsExporter;
import org.abx.webappgen.utils.SpecsImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.stringtemplate.v4.ST;

import java.util.HashMap;
import java.util.Set;

import static org.abx.webappgen.utils.ElementUtils.elementHashCode;
import static org.abx.webappgen.utils.ElementUtils.mapHashCode;

@RestController
@RequestMapping("/page")
public class PageController extends RoleController {

    @Autowired
    public PageModel pageModel;

    private HashMap<String, ST> pageTemplates;

    @Autowired
    private BinaryResourceRepository binaryResourceRepository;

    @Autowired
    private MapEntryRepository mapEntryRepository;

    public PageController() {
        pageTemplates = new HashMap<>();
    }

    @GetMapping(value = "/**", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("permitAll()")
    public Object page(HttpSession session, HttpServletRequest request) {
        String pagename = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
        );
        pagename = pagename.replaceFirst("/page/", "");
        if (pagename.contains("/")) {
            pagename = pagename.substring(0, pagename.indexOf("/"));
        }
        if (pagename.contains("?")) {
            pagename = pagename.substring(0, pagename.indexOf("?"));
        }
        if (!pageModel.validPage(getRoles(), elementHashCode(pagename))) {
            String home = pageModel.getHome();
            return new RedirectView("/page/" + home);
        }
        Set<String> roles = getRoles();
        ST st1 = getPageTemplate(session);
        String model= pageModel.getPageByPageMatchesId(roles, env(session),
                elementHashCode(pagename)).toString(2);
        return st1.add("pagespecs", model).render();
    }


    private ST getPageTemplate(HttpSession session) {
        SessionEnv sessionEnv = env(session);
        String laf = sessionEnv.get("laf");
        if (!pageTemplates.containsKey(laf)) {
            pageTemplates.put(laf, createPageTemplate(laf));

        }
        return new ST(pageTemplates.get(laf));
    }


    @Transactional
    protected ST createPageTemplate(String laf) {
        String value = mapEntryRepository.findByMapEntryId(mapHashCode("app.Env", "laf." + laf)).mapValue;
        long resourceId = elementHashCode(value);
        byte[] data = binaryResourceRepository.findByBinaryResourceId(resourceId).resourceValue;
        return new ST(new String(data), '{', '}');
    }


}
