package org.abx.webappgen.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.abx.util.Pair;
import org.abx.webappgen.persistence.ResourceModel;
import org.abx.webappgen.persistence.UserModel;
import org.abx.webappgen.persistence.dao.UserRepository;
import org.apache.tika.Tika;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.Set;

@RestController

@RequestMapping("/resources")
public class ResourceController extends RoleController {

    private static final Tika tika = new Tika();
    @Autowired
    private ResourceModel resourceModel;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserModel userModel;

    @GetMapping(value = "/texts/{resource}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public String textResource(@PathVariable String resource) {
        Set<String> roles = getRoles();
        JSONObject data = resourceModel.getTextResource(roles, resource);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found: " + resource);
        }
        return data.toString();
    }

    @GetMapping(value = "/text/{resource}", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("permitAll()")
    public String getText(@PathVariable String resource) {
        Set<String> roles = getRoles();
        String data = resourceModel.getText(roles, resource);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found: " + resource);
        }
        return data;
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
    @PostMapping(value = "/arrayPairs/{arrayPairName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String createArrayPair(
            @PathVariable String arrayPairName) {
        String packageName = arrayPairName.substring(0, arrayPairName.lastIndexOf('.'));
        JSONObject result = new JSONObject();
        try {
            resourceModel.createArrayPair(packageName, arrayPairName);
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
    @DeleteMapping(value = "/arrayPairs/{arrayPairName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteArrayPair(
            @PathVariable String arrayPairName) {
        JSONObject result = new JSONObject();
        try {
            resourceModel.deleteArrayPair(arrayPairName);
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
    @DeleteMapping(value = "/arrayPairs/{arrayName}/entries/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteArrayPairIndex(
            @PathVariable String arrayPairName,
            @PathVariable long key) {
        JSONObject status = new JSONObject();
        try {
            status.put("success", resourceModel.deleteArrayIndex(arrayPairName, key));
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
            status.put("error", "Failed to save map entry " + e.getMessage());
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
            status.put("error", "Failed to updates array entries " + e.getMessage());
        }
        return status.toString();
    }

    @Secured("Admin")
    @PostMapping(value = "/arrayPairs/{arrayPairName}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateArrayPairEntries(
            @PathVariable String arrayPairName,
            @RequestParam String values) {
        JSONObject status = new JSONObject();
        try {
            resourceModel.updateArrayPairEntries(arrayPairName, new JSONArray(values));
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", "Failed to updates array entries " + e.getMessage());
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
            status.put("error", "Failed to add array entry " + e.getMessage());
        }
        return status.toString();
    }

    @Secured("Admin")
    @PostMapping(value = "/arrayPairs/{arrayPairName}/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addArrayPairEntries(
            @PathVariable String arrayPairName,
            @RequestParam String key,
            @RequestParam String value) {
        JSONObject status = new JSONObject();
        try {
            resourceModel.addArrayPairEntry(arrayPairName, key, value);
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", "Failed to add array entry " + e.getMessage());
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
    @GetMapping(value = "/packages/arrayPairs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String arrayPairPackages() {
        return resourceModel.getArrayPairPackages().toString();
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
    @GetMapping(value = "/packages/arrayPairs/{packageName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String arrayPairsByPackage(@PathVariable String packageName) {
        return resourceModel.getArrayPairsByPackageName(packageName).toString(2);
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
    @GetMapping(value = "/packages/binaries/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public String binariesByPackage(HttpServletRequest request) {
        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
        );
        String packageName = path.replaceFirst("/resources/packages/binaries/", "");
        return resourceModel.getBinariesByPackageName(packageName).toString(2);
    }

    @Secured("Admin")
    @GetMapping(value = "/packages/js/{packageName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String jsByPackage(@PathVariable String packageName) {
        return resourceModel.getJSByPackageName(packageName).toString(2);
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
    @GetMapping(value = "/arrayPairs/{arrayPairName}/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getArrayPairEntriesCount(@PathVariable String arrayPairName) {
        return resourceModel.getArrayPairEntriesCount(arrayPairName);
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

    @Secured("Admin")
    @DeleteMapping(value = "/binaries/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteBinary(HttpServletRequest request) {
        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
        );

        // Remove the prefix "/binaries/"
        String resource = path.replaceFirst("/resources/binaries/", "");
        JSONObject status = new JSONObject();
        try {
            resourceModel.deleteBinaryResource(resource);
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }

    @Secured("Admin")
    @GetMapping(value = "/users/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getUsersCount() {
        return resourceModel.userCount();
    }

    @Secured("Admin")
    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public String createUser(
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam String role) {
        JSONObject status = new JSONObject();
        try {
            userModel.createUserIfNotFound(name, password, role);
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
        }
        return status.toString();
    }


    @PreAuthorize("permitAll()")
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
    public String updateBinary(
            HttpServletRequest request,
            @RequestParam String name,
            @RequestParam String role,
            @RequestParam(required = false) String owner,
            @RequestParam String contentType) {
        String packageName = name.substring(0, name.lastIndexOf('/'));
        JSONObject status = new JSONObject();
        try {
            if (owner == null) {
                owner = request.getUserPrincipal().getName();
            }
            resourceModel.saveBinaryResource(name, packageName, owner, contentType, role);
            status.put("success", true);
            status.put("package", packageName);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }

    @Secured("Admin")
    @PostMapping(value = "/js", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateJS(
            HttpServletRequest request,
            @RequestParam String name,
            @RequestParam String role,
            @RequestParam(required = false) String owner,
            @RequestParam String contentType,
            @RequestParam String content) {
        String packageName = name.substring(0, name.lastIndexOf('/'));
        JSONObject status = new JSONObject();
        try {
            if (owner == null) {
                owner = request.getUserPrincipal().getName();
            }
            resourceModel.saveBinaryResource(name, packageName, owner, contentType, role);
            resourceModel.upload(name, content.getBytes());
            status.put("success", true);
            status.put("package", packageName);
            status.put("owner", owner);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }

    @Secured("Admin")
    @PostMapping(value = "/binaries/new",
            consumes = "multipart/form-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public String handleNew(
            HttpServletRequest request,
            @RequestPart String name,
            @RequestPart MultipartFile data) {
        JSONObject status = new JSONObject();
        try {
            String owner = request.getUserPrincipal().getName();
            String packageName = name.substring(0, name.lastIndexOf('/'));
            byte[] bytes = data.getBytes();
            String role = "Anonymous";
            String contentType = tika.detect(bytes);
            resourceModel.saveBinaryResource(name, packageName, owner, contentType, role);
            resourceModel.upload(name, bytes);
            status.put("package", packageName);
            status.put("owner", owner);
            status.put("contentType", contentType);
            status.put("role", role);
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }


    @Secured("Admin")
    @PostMapping(value = "/binaries/upload",
            consumes = "multipart/form-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public String handleUpload(
            @RequestPart String name,
            @RequestPart(required = false) MultipartFile data) {
        JSONObject status = new JSONObject();
        try {
            byte[] bytes = data.getBytes();
            resourceModel.upload(name, bytes);
            status.put("success", true);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }

    @Secured("Admin")
    @PostMapping(value = "/binaries/clone", produces = MediaType.APPLICATION_JSON_VALUE)
    public String clone(
            @RequestParam String original,
            @RequestParam String newName,
            @RequestParam String role,
            @RequestParam String owner,
            @RequestParam String contentType) throws IOException {
        String packageName = newName.substring(0, newName.lastIndexOf('/'));
        JSONObject status = new JSONObject();
        try {
            resourceModel.cloneBinary(original, newName, packageName, owner, contentType, role);
            status.put("success", true);
            status.put("package", packageName);
        } catch (Exception e) {
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        return status.toString();
    }

}
