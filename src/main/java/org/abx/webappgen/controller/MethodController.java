package org.abx.webappgen.controller;

import org.abx.webappgen.persistence.MethodModel;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.dao.MethodSpecRepository;
import org.abx.webappgen.persistence.model.MethodSpec;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static org.abx.webappgen.persistence.PageModel.elementHashCode;

@RestController
public class MethodController extends RoleController {

    @Autowired
    public MethodModel methodModel;

    @Autowired
    public PageModel pageModel;


    @GetMapping(value = "/process/{methodName}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<byte[]> page(@PathVariable String methodName) {
        JSONObject methodSpecs = methodModel.getMethodSpec(methodName);
        if (methodSpecs == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Method " + methodName + " not found");
        }
        Set<String> roles = getRoles();
        String requiredRole = methodSpecs.getString("role");
        if (!roles.contains(requiredRole)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType(methodSpecs.getString("setContentType"))); // Or your custom type
        headers.setContentDispositionFormData("attachment",
                methodSpecs.getString("outputName"));

        String r = new JSONObject(roles).toString();
        headers.setContentLength(r.length());
        return new ResponseEntity<>(r.getBytes(), headers, HttpStatus.OK);
    }
}
