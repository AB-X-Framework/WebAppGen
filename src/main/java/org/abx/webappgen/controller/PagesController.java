package org.abx.webappgen.controller;

import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.utils.SpecsExporter;
import org.abx.webappgen.utils.SpecsImporter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pages")
public class PagesController extends RoleController {
    @Autowired
    private SpecsExporter specsExporter;

    @Autowired
    private SpecsImporter specsImporter;

    @Autowired
    public PageModel pageModel;

    public PagesController() {

    }


    @GetMapping(value = "/packages", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String packages() {
        return pageModel.getPagePackages().toString(2);
    }

    @GetMapping(value = "/packages/{packageName}/pages", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String byPackage(@PathVariable String packageName) {
        return pageModel.getPageNames(packageName).toString(2);
    }

    @GetMapping(value = "/details/{pageName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String getPageDetails(@PathVariable String pageName) {
        return specsExporter.getPageDetails(pageName).toString(2);
    }


    @PostMapping(value = "/clone", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String clonePage(@RequestParam String page, @RequestParam String newName) {
        JSONObject status = new JSONObject();
        try {
            String packageName = newName.substring(0, newName.lastIndexOf("."));
            JSONObject jsonPage = new JSONObject(page);
            jsonPage.put("packageName", packageName);
            jsonPage.put("name", newName);
            specsImporter.processPage(packageName, jsonPage);
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", true);
            status.put("message", e.getMessage());
        }
        return status.toString();
    }

}
