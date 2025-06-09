package org.abx.webappgen.controller;

import org.abx.webappgen.persistence.MethodModel;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.UserModel;
import org.abx.webappgen.utils.MethodExecuter;
import org.abx.webappgen.utils.SpecsExporter;
import org.abx.webappgen.utils.SpecsImporter;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
