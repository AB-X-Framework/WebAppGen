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

@RequestMapping("/resources")
public class ResourceController extends RoleController {

    @Autowired
    private ResourceModel resourceModel;

    @GetMapping(value = "/text/{resource}", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("permitAll()")
    public ResponseEntity<String>  page(@PathVariable String resource) {
        Set<String> roles = getRoles();
        String data = resourceModel.getTextResource(roles,resource);
        if (data == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN); // Or your custom type
        headers.setContentDispositionFormData("attachment", resource);
        headers.setContentLength(data.length());
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @GetMapping("/binary/{resource}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String resource) {
        Set<String> roles = getRoles();
        Pair<String, byte[]> fileContent = resourceModel.getBinaryResource(roles, resource);
        if (fileContent == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(fileContent.first)); // Or your custom type
        headers.setContentDispositionFormData("attachment", resource);
        headers.setContentLength(fileContent.second.length);
        return new ResponseEntity<>(fileContent.second, headers, HttpStatus.OK);
    }

    @Secured("Admin")
    @PostMapping(value = "/upload/binary", consumes = "multipart/form-data")
    public long handleUpload(
            @RequestPart MultipartFile data,
            @RequestPart String packageName,
            @RequestPart String role,
            @RequestPart String contentType) throws IOException {
        byte[] fileBytes = data.getBytes();
        String name = data.getOriginalFilename();
        return resourceModel.saveBinaryResource(name, packageName,contentType, fileBytes, role);
    }
}
