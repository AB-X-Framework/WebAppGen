package org.abx.webappgen.controller;

import org.abx.util.Pair;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.model.ResourceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/page")
public class ResourceController {

    @Autowired
    private ResourceModel resourceModel;

    @GetMapping(value = "/text/{resource}", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("permitAll()")
    public String page(@PathVariable String resource) {
        return resourceModel.getTextResource(PageModel.elementHashCode(resource));
    }

    @GetMapping("/binary/{resource}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String resource) {
        Pair<String, byte[]> fileContent = resourceModel.getBinary(PageModel.elementHashCode(resource));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF); // Or your custom type
        headers.setContentDispositionFormData("attachment", resource);
        headers.setContentLength(fileContent.second.length);
        return new ResponseEntity<>(fileContent.second, headers, HttpStatus.OK);
    }
}
