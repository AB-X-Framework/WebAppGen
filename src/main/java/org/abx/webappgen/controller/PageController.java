package org.abx.webappgen.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.abx.webappgen.persistence.EnvListener;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.ResourceModel;
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
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.abx.webappgen.persistence.ResourceModel.AppEnv;
import static org.abx.webappgen.persistence.ResourceModel.BinaryResources;
import static org.abx.webappgen.utils.ElementUtils.elementHashCode;
import static org.abx.webappgen.utils.ElementUtils.mapHashCode;

@RestController
@RequestMapping("/page")
public class PageController extends RoleController implements EnvListener {

    @Autowired
    public PageModel pageModel;

    @Autowired
    public ResourceModel resourceModel;
    private HashMap<String, ST> pageTemplates;

    @Autowired
    private BinaryResourceRepository binaryResourceRepository;

    @Autowired
    private MapEntryRepository mapEntryRepository;

    public PageController() {
        pageTemplates = new HashMap<>();
    }

    @PostConstruct
    public void post() {
        resourceModel.addEnvListener(this);
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
        String model = pageModel.getPageByPageMatchesId(roles, env(session),
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

    @Override
    public void envChanged() {
        pageTemplates.clear();
    }

    @Transactional
    protected ST createPageTemplate(String laf) {
        String value = mapEntryRepository.findByMapEntryId(mapHashCode(AppEnv, "laf." + laf)).mapValue;
        long resourceId = elementHashCode(value);
        resourceModel.addResourceListener(resourceId, this);
        byte[] data = binaryResourceRepository.findByBinaryResourceId(resourceId).resourceValue;
        return encode(new String(data));
    }

    private ST encode(String template) {
        Pattern p = Pattern.compile("(?<!\\\\)\\{([^}]+)\\}");
        Matcher m = p.matcher(template);

        Set<String> attrs = new HashSet<>();
        while (m.find()) {
            attrs.add(m.group(1).trim());
        }
        ST st = new ST(new String(template), '{', '}');
        for (String attr : attrs) {
            if (attr.startsWith(BinaryResources)) {
                String resource = attr.substring(BinaryResources.length());
                long id = elementHashCode(resource);
                resourceModel.addResourceListener(id, this);
                long hash = resourceModel.getBinaryResource(resource).getLong("hashcode");
                st.add(attr, "/resources/binary/" + resource + "?hc=" + hash);
            }
        }
        return st;
    }


}
