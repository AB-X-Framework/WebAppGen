package org.abx.webappgen.controller;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.abx.webappgen.persistence.MethodModel;
import org.abx.webappgen.persistence.PageModel;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class MethodController extends RoleController {

    @Autowired
    public MethodModel methodModel;

    @Autowired
    public PageModel pageModel;


    @PostMapping(value = "/process/{methodName}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<byte[]> page(@PathVariable String methodName, @RequestParam String args) {
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
        headers.setContentDispositionFormData("attachment",
                methodSpecs.getString("outputName"));

        try {
            Object obj = processMethod(methodSpecs, args);
            byte[] data = serialize(type, obj);
            headers.setContentLength(data.length);

            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private byte[] serialize(String type, Object obj) throws Exception {
        switch (type) {
            case "json":
            case "string":
                return obj.toString().getBytes();
            case "png":
            case "jpg":
                return toBytes((BufferedImage) obj,type);
            default:
                return (byte[]) obj;
        }
    }

    public static byte[] toBytes(BufferedImage image, String format)
            throws IOException {
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
        switch (type) {
            case "string":
                return MediaType.TEXT_PLAIN;
            case "json":
                return MediaType.APPLICATION_JSON;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
                return MediaType.IMAGE_JPEG;
            case "pdf":
                return MediaType.APPLICATION_PDF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private Object processMethod(JSONObject methodSpecs, String args) throws Exception {
        Context cx = Context.newBuilder("js").allowExperimentalOptions(true).option("js.operator-overloading", "true")
                .allowAllAccess(true)
                .build();
        cx.enter();
        Value jsBindings = cx.getBindings("js");
        String methodName = methodSpecs.getString("name");
        JSONObject jsonArgs = methodSpecs.getJSONObject("args");
        for (String arg : jsonArgs.keySet()) {
            jsBindings.putMember(arg, jsonArgs.get(arg));
        }
        String fullSources = "function " + methodName + "(){" +
                methodSpecs.getString("methodJS") + "} " + methodName + "();";
        Source source = Source.newBuilder("js", fullSources, methodName).build();
        return cx.eval(source);
    }
}
