package org.abx.webappgen.controller;

import org.abx.util.StreamUtils;
import org.abx.webappgen.persistence.UserPageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stringtemplate.v4.ST;

import java.io.IOException;

@RestController
@RequestMapping("/page")
public class UserPageController {

    private  ST pageTemplate ;

    @Autowired
    public UserPageModel userPageModel;
    public UserPageController(){
        try {
            String data = StreamUtils.readResource("org/abx/webappgen/page.html");
            pageTemplate = new ST(data,'{', '}');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/{pagename}", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("permitAll()")
    public String page(@PathVariable String pagename) {
        ST st1 = new ST(pageTemplate);
        String output = st1.add("pagename",pagename).render();
        return output;
    }

    @GetMapping(value = "/specs/{pagename}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public String pageSpecs(@PathVariable String pagename) {
        return userPageModel.getPageByPageId(userPageModel.elementHashCode(pagename)).toString(1);
    }
}
