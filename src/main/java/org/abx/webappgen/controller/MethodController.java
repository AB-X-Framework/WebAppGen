package org.abx.webappgen.controller;

import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.dao.MethodSpecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.stringtemplate.v4.ST;

@RestController
public class MethodController {

    @Autowired
    public MethodSpecRepository methodSpecRepository;

    @Autowired
    public PageModel pageModel;


    @GetMapping(value = "/process/{methodName}")
    @PreAuthorize("permitAll()")
    public String page(@PathVariable String methodName) {
    }
}
