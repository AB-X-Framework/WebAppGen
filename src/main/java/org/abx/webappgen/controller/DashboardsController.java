package org.abx.webappgen.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class DashboardsController {


    @GetMapping(value = "/dashboards", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("User")
    public String dashboards(final HttpServletRequest request) {
        return "hr";
    }


}
