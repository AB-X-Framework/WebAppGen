package org.abx.webappgen.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.abx.util.Pair;
import org.abx.webappgen.persistence.ResourceModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.Set;

@RestController

@RequestMapping("/resources")
public class ResourceController extends RoleController {

    @Autowired
    private ResourceModel resourceModel;

    @GetMapping(value = "/texts/{resource}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String textResource(@PathVariable String resource) {
        Set<String> roles = getRoles();
        JSONObject data = resourceModel.getTextResource(roles, resource);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found: " + resource);
        }
        return data.toString();
    }


    @GetMapping(value = "/methods/{resource}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("Admin")
    public String methodResource(@PathVariable String resource) {
        JSONObject data = resourceModel.getMethodResource(resource);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found: " + resource);
        }
        return data.toString();
    }

    @Secured("Admin")
    @DeleteMapping(value = "/texts/{resourceName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteText(
            @PathVariable String resourceName) {
        JSONObject status = new JSONObject();
        try {
            status.put("success", resourceModel.deleteText(resourceName));
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }

    @Secured("Admin")
    @DeleteMapping(value = "/methods/{resourceName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteMethod(
            @PathVariable String resourceName) {
        JSONObject status = new JSONObject();
        try {
            status.put("success", resourceModel.deleteMethod(resourceName));
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }


    @Secured("Admin")
    @PostMapping(value = "/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addText(
            final HttpServletRequest request,
            @RequestParam String resource) {
        JSONObject status = new JSONObject();
        JSONObject jsonResource = new JSONObject(resource);
        if (jsonResource.isNull("owner")) {
            jsonResource.put("owner", request.getUserPrincipal().getName());
        }
        try {
            String name = jsonResource.getString("name");
            String packageName = name.substring(0, name.lastIndexOf('.'));
            jsonResource.put("package", packageName);
            resourceModel.addTextResource(jsonResource);
            status.put("package", packageName);
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }


    @Secured("Admin")
    @PostMapping(value = "/methods", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addMethod(
            @RequestParam String resource) {
        JSONObject status = new JSONObject();
        JSONObject jsonResource = new JSONObject(resource);
        try {
            String name = jsonResource.getString("name");
            String packageName = name.substring(0, name.lastIndexOf('.'));
            jsonResource.put("package", packageName);
            resourceModel.addMethodResource(jsonResource);
            status.put("package", packageName);
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }


    @Secured("Admin")
    @PostMapping(value = "/maps/{mapName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String createMap(
            @PathVariable String mapName) {
        String packageName = mapName.substring(0, mapName.lastIndexOf('.'));
        JSONObject result = new JSONObject();
        try {
            resourceModel.createMap(packageName, mapName);
            result.put("success", true);
            result.put("package", packageName);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result.toString();
    }

    @Secured("Admin")
    @PostMapping(value = "/arrays/{arrayName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String createArray(
            @PathVariable String arrayName) {
        String packageName = arrayName.substring(0, arrayName.lastIndexOf('.'));
        JSONObject result = new JSONObject();
        try {
            resourceModel.createArray(packageName, arrayName);
            result.put("success", true);
            result.put("package", packageName);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result.toString();
    }

    @Secured("Admin")
    @DeleteMapping(value = "/maps/{mapName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteMap(
            @PathVariable String mapName) {
        JSONObject result = new JSONObject();
        try {
            resourceModel.deleteMap(mapName);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result.toString();
    }


    @Secured("Admin")
    @DeleteMapping(value = "/arrays/{arrayName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteArray(
            @PathVariable String arrayName) {
        JSONObject result = new JSONObject();
        try {
            resourceModel.deleteArray(arrayName);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result.toString();
    }


    @Secured("Admin")
    @GetMapping(value = "/maps/{mapName}/entries/{key}")
    public String downloadMapValue(
            @PathVariable String mapName,
            @PathVariable String key) {
        return resourceModel.getMapResource(mapName, key);
    }

    @Secured("Admin")
    @DeleteMapping(value = "/maps/{mapName}/entries/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteMapValue(
            @PathVariable String mapName,
            @PathVariable String key) {
        JSONObject status = new JSONObject();
        try {
            status.put("success", resourceModel.deleteMapEntry(mapName, key));
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }

    @Secured("Admin")
    @DeleteMapping(value = "/arrays/{arrayName}/entries/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteArrayIndex(
            @PathVariable String arrayName,
            @PathVariable long key) {
        JSONObject status = new JSONObject();
        try {
            status.put("success", resourceModel.deleteArrayIndex(arrayName, key));
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }


    @Secured("Admin")
    @PostMapping(value = "/maps/{mapName}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    public String saveMapEntries(
            @PathVariable String mapName,
            @RequestParam String values) {
        JSONObject status = new JSONObject();
        try {
            resourceModel.saveMapEntries(mapName, new JSONArray(values));
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("message", "Failed to save map entry " + e.getMessage());
        }
        return status.toString();
    }

    @Secured("Admin")
    @PostMapping(value = "/arrays/{arrayName}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateArrayEntries(
            @PathVariable String arrayName,
            @RequestParam String values) {
        JSONObject status = new JSONObject();
        try {
            resourceModel.updateArrayEntries(arrayName, new JSONArray(values));
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("message", "Failed to updates array entries " + e.getMessage());
        }
        return status.toString();
    }


    @Secured("Admin")
    @PostMapping(value = "/arrays/{arrayName}/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addArrayEntries(
            @PathVariable String arrayName,
            @RequestParam String value) {
        JSONObject status = new JSONObject();
        try {
            resourceModel.addArrayEntry(arrayName, value);
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("message", "Failed to add array entry " + e.getMessage());
        }
        return status.toString();
    }

    @Secured("Admin")
    @GetMapping(value = "/packages/maps", produces = MediaType.APPLICATION_JSON_VALUE)
    public String mapPackages() {
        return resourceModel.getMapPackages().toString();
    }

    @Secured("Admin")
    @GetMapping(value = "/packages/arrays", produces = MediaType.APPLICATION_JSON_VALUE)
    public String arrayPackages() {
        return resourceModel.getArrayPackages().toString();
    }

    @Secured("Admin")
    @GetMapping(value = "/packages/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public String textPackages() {
        return resourceModel.getTextPackages().toString();
    }

    @Secured("Admin")
    @GetMapping(value = "/packages/methods", produces = MediaType.APPLICATION_JSON_VALUE)
    public String methodPackages() {
        return resourceModel.getMethodPackages().toString();
    }


    @Secured("Admin")
    @GetMapping(value = "/packages/binaries", produces = MediaType.APPLICATION_JSON_VALUE)
    public String binariesPackages() {
        return resourceModel.getBinaryPackages().toString();
    }

    @Secured("Admin")
    @GetMapping(value = "/packages/maps/{packageName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String mapsByPackage(@PathVariable String packageName) {
        return resourceModel.getMapsByPackageName(packageName).toString(2);
    }

    @Secured("Admin")
    @GetMapping(value = "/packages/arrays/{packageName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String arraysByPackage(@PathVariable String packageName) {
        return resourceModel.getArraysByPackageName(packageName).toString(2);
    }


    @Secured("Admin")
    @GetMapping(value = "/packages/texts/{packageName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String textsByPackage(@PathVariable String packageName) {
        return resourceModel.getTextsByPackageName(packageName).toString(2);
    }


    @Secured("Admin")
    @GetMapping(value = "/packages/methods/{packageName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String methodsByPackage(@PathVariable String packageName) {
        return resourceModel.getMethodsByPackageName(packageName).toString(2);
    }

    @Secured("Admin")
    @GetMapping(value = "/packages/binaries/{packageName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String binariesByPackage(@PathVariable String packageName) {
        return resourceModel.getBinariesByPackageName(packageName).toString(2);
    }

    @Secured("Admin")
    @GetMapping(value = "/binaryContentTypes", produces = MediaType.APPLICATION_JSON_VALUE)
    public String binariesByPackage() {
        return resourceModel.getBinariesOutputTypes().toString(2);
    }

    @Secured("Admin")
    @GetMapping(value = "/maps/{mapName}/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getMapEntriesCount(@PathVariable String mapName) {
        return resourceModel.getMapEntriesCount(mapName);
    }

    @Secured("Admin")
    @GetMapping(value = "/arrays/{arrayName}/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getArrayEntriesCount(@PathVariable String arrayName) {
        return resourceModel.getArrayEntriesCount(arrayName);
    }

    @Secured("Admin")
    @GetMapping(value = "/arrays/{arrayName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getEntireArrayEntries(@PathVariable String arrayName) {
        return resourceModel.getEntireArrayEntries(arrayName).toString();
    }

    @Secured("Admin")
    @GetMapping(value = "/binaries/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBinaryResource(HttpServletRequest request) {
        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
        );

        // Remove the prefix "/binaries/"
        String resource = path.replaceFirst("/resources/binaries/", "");
        return resourceModel.getBinaryResource(resource).toString();
    }


    @GetMapping("/binary/**")
    public ResponseEntity<?> getBinary(HttpServletRequest request) {
        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
        );

        // Remove the prefix "/binaries/"
        String resource = path.replaceFirst("/resources/binary/", "");
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
    @PostMapping(value = "/binaries", produces = MediaType.APPLICATION_JSON_VALUE)
    public String handleUpload(
            @RequestPart String name,
            @RequestPart String role,
            @RequestPart String owner,
            @RequestPart String contentType) {
        String packageName = name.substring(0, name.lastIndexOf('.'));
        JSONObject status = new JSONObject();

        try {
            status.put("success", true);
            resourceModel.saveBinaryResource(name, packageName, owner, contentType, role);
        } catch (Exception e) {
            status.put("success", false);
            status.put("message", e.getMessage());
        }
        return status.toString();
    }


    @Secured("Admin")
    @PostMapping(value = "/binaries/{binaryName}",
            consumes = "multipart/form-data",produces = MediaType.APPLICATION_JSON_VALUE)
    public String handleUpload(
            @PathVariable String binaryName,
            @RequestPart(required = false) MultipartFile data) throws Exception {
        JSONObject status = new JSONObject();
        try {
            status.put("success", true);
            byte[] bytes = data.getBytes();
            resourceModel.upload(binaryName, bytes);
        } catch (Exception e) {
            status.put("success", false);
            status.put("message", e.getMessage());
        }
        return status.toString();
    }

    @Secured("Admin")
    @PostMapping(value = "/binaries/clone")
    public long clone(
            HttpServletRequest request,
            @RequestParam String original,
            @RequestParam String newName,
            @RequestParam String role,
            @RequestParam String owner,
            @RequestParam String contentType) throws IOException {
        String packageName = newName.substring(0, newName.lastIndexOf('/'));
        if (owner == null) {
            owner = request.getUserPrincipal().getName();
        }
        return resourceModel.cloneBinary(original, newName, packageName, owner, contentType, role);
    }

}
