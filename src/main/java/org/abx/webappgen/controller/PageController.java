package org.abx.webappgen.controller;

import jakarta.servlet.http.HttpSession;
import org.abx.util.StreamUtils;
import org.abx.webappgen.persistence.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/page")
public class PageController extends RoleController{
    public final static String LANG = "lang";

    private ST pageTemplate;

    @Autowired
    public PageModel pageModel;

    public PageController() {
        try {
            String data = StreamUtils.readResource("org/abx/webappgen/page.html");
            pageTemplate = new ST(data, '{', '}');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/{pagename}", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("permitAll()")
    public String page(@PathVariable String pagename) {
        ST st1 = new ST(pageTemplate);
        String output = st1.add("pagename", pagename).render();
        return output;
    }


    @GetMapping(value = "/specs/{pagename}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public String pageSpecs(@PathVariable String pagename, HttpSession session) {
        Set<String> roles = getRoles();
        return pageModel.getPageByPageId(roles, env(session),
                pageModel.elementHashCode(pagename)).toString(1);
    }


    private String env(HttpSession session) {
        String env = "";
        if (session.getAttribute(LANG) != null) {
            env += (String) session.getAttribute(LANG);
        } else {
            env += "ES";
        }
        return env;
    }
}
