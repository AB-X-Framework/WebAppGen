package org.abx.webappgen.controller;

import org.abx.util.StreamUtils;
import org.abx.webappgen.creds.model.User;
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
        String output = pageTemplate.add("pagename",pagename).render();
        return output;
    }


}
