package org.abx.webappgen.controller;

import jakarta.servlet.http.HttpSession;
import org.abx.util.StreamUtils;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.utils.SpecsExporter;
import org.abx.webappgen.utils.SpecsImporter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/page")
public class PageController extends RoleController{
    public final static String LANG = "lang";

    private ST pageTemplate;

    @Autowired
    private SpecsExporter specsExporter;

    @Autowired
    private SpecsImporter specsImporter;

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
        if (!pageModel.validPage(getRoles(), pageModel.elementHashCode(pagename))){
            pagename= pageModel.getHome();
        }
        ST st1 = new ST(pageTemplate);
        String output = st1.add("pagename", pagename).render();
        return output;
    }


    @GetMapping(value = "/packages", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String packages() {
        return pageModel.getPackages().toString(2);
    }



    @GetMapping(value = "/packages/{packageName}/components", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String byPackage(@PathVariable String packageName) {
        return pageModel.getComponentNames(packageName).toString(2);
    }

    @GetMapping(value = "/components/{componentName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String getComponentDetails(@PathVariable String componentName)  {
        return specsExporter.getComponentDetails(componentName).toString(2);
    }


    @PostMapping(value = "/components", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String saveComponent(@RequestParam String  component)  {
        JSONObject status=new JSONObject();
        try {
            specsImporter.save(new JSONObject(component));
            status.put("success","true");
        }catch (Exception e){
            status.put("success","false");
            status.put("error", e.getMessage());
        }
        return status.toString(2);
    }

    @GetMapping(value = "/specs/{pagename}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public String pageSpecs(@PathVariable String pagename, HttpSession session) {
        Set<String> roles = getRoles();
        return pageModel.getPageByPageMatchesId(roles, env(session),
                pageModel.elementHashCode(pagename)).toString(2);
    }

    @GetMapping(value = "/component/{componentName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String component(@PathVariable String componentName, HttpSession session) {
        return pageModel.getComponentByName(componentName, env(session)).toString(2);
    }


    private String env(HttpSession session) {
        StringBuilder env = new StringBuilder();
        if (session.getAttribute(LANG) != null) {
            env .append((String) session.getAttribute(LANG));
        }
        for (String role : getRoles()) {
            env.append("|").append(role);
        }
        return env.toString();
    }
}
