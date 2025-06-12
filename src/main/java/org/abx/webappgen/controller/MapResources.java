package org.abx.webappgen.controller;

import org.abx.util.Pair;
import org.abx.webappgen.persistence.ResourceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RestController

@RequestMapping("/maps")
public class MapResources extends RoleController {

    @Autowired
    private ResourceModel resourceModel;

    @Secured("Admin")
    @GetMapping(value = "/get/{resource}/{key}")
    public String downloadMapValue(
            @PathVariable String resource,
            @PathVariable String key)  {
        return resourceModel.getMapResource(resource,key);
    }

    @Secured("Admin")
    @GetMapping(value = "/packages",produces  = MediaType.APPLICATION_JSON_VALUE)
    public String downloadMaps()  {
        return resourceModel.getMapPackages().toString();
    }
}
