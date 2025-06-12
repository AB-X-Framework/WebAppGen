package org.abx.webappgen.controller;

import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.utils.SpecsExporter;
import org.abx.webappgen.utils.SpecsImporter;
import org.json.JSONArray;
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


    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String savePage(@RequestParam String page) {
        JSONObject status = new JSONObject();
        try {
            specsImporter.savePage(new JSONObject(page));
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString(2);
    }

    @PostMapping(value = "/clone", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String clone(@RequestParam String newName, @RequestParam String page) {
        JSONObject jsonPage = new JSONObject(page);
        String packageName = newName.substring(0, newName.lastIndexOf("."));
        jsonPage.put("package", packageName);
        jsonPage.put("name", newName);
        JSONObject status = new JSONObject();
        try {
            specsImporter.savePage(jsonPage);
            status.put("page", jsonPage);
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString(2);
    }


    @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String newPage(@RequestParam String newName) {
        String packageName = newName.substring(0, newName.lastIndexOf("."));
        JSONObject status = new JSONObject();
        try {
            JSONObject page = new JSONObject();
            page.put("name", newName);
            page.put("title", "New Page");
            page.put("package", packageName);
            page.put("matches","/Page"+((int)(Math.random()*900000)));
            page.put("css",new JSONArray());
            page.put("scripts",new JSONArray());
            page.put("component","app.Default");
            page.put("role","Admin");

            specsImporter.savePage(page);
            status.put("success", "true");
            status.put("page", page);
        } catch (Exception e) {
            status.put("success", "false");
            status.put("error", e.getMessage());
        }
        return status.toString(2);
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

}
