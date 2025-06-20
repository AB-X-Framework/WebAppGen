package org.abx.webappgen.controller;

import jakarta.servlet.http.HttpSession;
import org.abx.util.StreamUtils;
import org.abx.webappgen.utils.SpecsExporter;
import org.abx.webappgen.utils.SpecsImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.util.Set;

import static org.abx.webappgen.utils.ElementUtils.elementHashCode;

@RestController
@RequestMapping("/page")
public class PageController extends RoleController {

    private ST pageTemplate;

    @Autowired
    private SpecsExporter specsExporter;

    @Autowired
    private SpecsImporter specsImporter;


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
        if (!pageModel.validPage(getRoles(), elementHashCode(pagename))) {
            pagename = pageModel.getHome();
        }
        ST st1 = new ST(pageTemplate);
        String output = st1.add("pagename", pagename).render();
        return output;
    }


    @GetMapping(value = "/specs/{pagename}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public String pageSpecs(@PathVariable String pagename, HttpSession session) {
        Set<String> roles = getRoles();
        return pageModel.getPageByPageMatchesId(roles, env(session),
                elementHashCode(pagename)).toString(2);
    }


}
