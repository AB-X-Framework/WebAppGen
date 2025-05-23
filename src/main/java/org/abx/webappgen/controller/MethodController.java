package org.abx.webappgen.controller;

import org.abx.webappgen.persistence.MethodModel;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.UserModel;
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

@RestController
@RequestMapping("/process")
public class MethodController extends RoleController {

    @Autowired
    public MethodModel methodModel;

    @Autowired
    public PageModel pageModel;

    @Autowired
    public UserModel userModel;

    @Autowired
    public SpecsImporter specsImporter;
    @Autowired
    public SpecsExporter specsExporter;


    @PostMapping(value = "/{methodName}", consumes = "multipart/form-data")
    @PreAuthorize("permitAll()")
    public ResponseEntity<byte[]> method(@PathVariable String methodName, @RequestPart(required = false) MultipartFile data, @RequestPart String args) {
        JSONObject methodSpecs = methodModel.getMethodSpec(methodName);
        if (methodSpecs == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String requiredRole = methodSpecs.getString("role");
        if (!requiredRole.equals("Anonymous")) {
            if (!getRoles().contains(requiredRole)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        String type = methodSpecs.getString("type");
        headers.setContentType(contentType(type)); // Or your custom type
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + methodSpecs.getString("outputName") + "\"");

        try {
            Object obj = processMethod(methodName, methodSpecs, data, args);
            byte[] output = serialize(type, obj);
            headers.setContentLength(output.length);
            return new ResponseEntity<>(output, headers, HttpStatus.OK);
        } catch (Exception e) {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<>(e.getMessage().getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private byte[] serialize(String type, Object obj) throws Exception {
        switch (type.intern()) {
            case "json":
            case "text":
                return obj.toString().getBytes();
            case "png":
            case "jpg":
                return toBytes((BufferedImage) obj, type);
            default:
                return ((Value) obj).as(byte[].class);
        }
    }

    public static byte[] toBytes(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean result = ImageIO.write(image, format, baos);
        if (!result) {
            throw new IOException("Unsupported image format: " + format);
        }
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();
        return imageBytes;
    }

    private MediaType contentType(String type) {
        switch (type.intern()) {
            case "text":
                return MediaType.TEXT_PLAIN;
            case "json":
                return MediaType.APPLICATION_JSON;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
                return MediaType.IMAGE_JPEG;
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "zip":
                return MediaType.valueOf("application/zip");
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private String expandDotPath(String path) {
        String[] keys = path.split("\\.");
        StringBuilder output = new StringBuilder();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < keys.length - 1; i++) {
            if (i > 0) current.append(".");
            current.append(keys[i]);
            output.append(current).append("={}\n");
        }

        // Final assignment
        output.append(current).append(".").append(keys[keys.length - 1]).append("=").append(keys[keys.length - 1]);

        return output.toString();
    }

    private Object processMethod(String methodName, JSONObject methodSpecs, MultipartFile data, String args) throws Exception {
        Context cx = Context.newBuilder("js").allowExperimentalOptions(true).option("js.operator-overloading", "true").allowAllAccess(true).build();
        cx.enter();
        Value jsBindings = cx.getBindings("js");
        JSONObject jsonArgs = new JSONObject(args);
        jsBindings.putMember("pageModel", pageModel);
        jsBindings.putMember("userModel", userModel);
        if (data != null) {
            jsBindings.putMember("data", data.getBytes());
        }
        jsBindings.putMember("utils", new MethodUtils());
        jsBindings.putMember("specsExporter", specsExporter);
        jsBindings.putMember("specsImporter", specsImporter);
        for (String arg : jsonArgs.keySet()) {
            jsBindings.putMember(arg, jsonArgs.get(arg));
        }
        String fullSources = methodSpecs.getString("methodJS");
        Source source = Source.newBuilder("js", fullSources, methodName).build();
        cx.eval(source);

        fullSources = expandDotPath(methodName);
        source = Source.newBuilder("js", fullSources, methodName).build();
        cx.eval(source);

        fullSources = methodName+"();";
        source = Source.newBuilder("js", fullSources, methodName).build();
        return cx.eval(source);
    }
}
