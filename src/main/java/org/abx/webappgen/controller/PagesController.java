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

import static org.abx.webappgen.utils.ElementUtils.elementHashCode;

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


}
