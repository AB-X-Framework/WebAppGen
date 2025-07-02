package org.abx.webappgen.controller;

import org.abx.webappgen.utils.MethodExecuter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/process")
public class MethodController extends RoleController {


    @Autowired
    public MethodExecuter executer;

    @PostMapping(value = "/{methodName}", consumes = "multipart/form-data")
    @PreAuthorize("permitAll()")
    public ResponseEntity<byte[]> method(@PathVariable String methodName,
                                         @RequestParam(required = false) Map<String, MultipartFile> files,
                                         @RequestPart(required = false) String args) {
        return executer.method(methodName, args, getRoles(), files);
    }

}
