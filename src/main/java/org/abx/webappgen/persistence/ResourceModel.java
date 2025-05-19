package org.abx.webappgen.persistence;

import org.abx.webappgen.persistence.dao.ArrayEntryRepository;
import org.abx.webappgen.persistence.dao.ArrayResourceRepository;
import org.abx.webappgen.persistence.dao.BinaryResourceRepository;
import org.abx.webappgen.persistence.dao.TextResourceRepository;
import org.abx.util.Pair;
import org.abx.webappgen.persistence.model.ArrayEntry;
import org.abx.webappgen.persistence.model.ArrayResource;
import org.abx.webappgen.persistence.model.BinaryResource;
import org.abx.webappgen.persistence.model.TextResource;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ResourceModel {
    @Autowired
    TextResourceRepository textResourceRepository;
    @Autowired
    BinaryResourceRepository binaryResourceRepository;
    @Autowired
    private ArrayResourceRepository arrayResourceRepository;
    @Autowired
    private ArrayEntryRepository arrayEntryRepository;

    public String getTextResource(Set<String> roles,String resourceName) {
        TextResource text = textResourceRepository.findByTextResourceId(
                PageModel.elementHashCode(resourceName)
        );

        if (text == null) {
            return null;
        }
        String requiredRole = text.role;
        if (!requiredRole.equals("Anonymous")) {
            if (!roles.contains(requiredRole)) {
                return null;
            }
        }
        return text.resourceValue;
    }

    public Pair<String, byte[]> getBinaryResource(Set<String> roles, String resourceName) {
        BinaryResource binaryResource = binaryResourceRepository.findByBinaryResourceId(
                PageModel.elementHashCode(resourceName));
        if (binaryResource == null) {
            return null;
        }
        String requiredRole = binaryResource.role;
        if (!requiredRole.equals("Anonymous")) {
            if (!roles.contains(requiredRole)) {
                return null;
            }
        }
        return new Pair<>(binaryResource.contentType, binaryResource.resourceValue);
    }

    public long saveBinaryResource(String resourceName,String contentType, byte[] data,String role) {
        long id =   PageModel.elementHashCode(resourceName);
        BinaryResource binaryResource = new BinaryResource();
        binaryResource.binaryResourceId=id;
        binaryResource.contentType=contentType;
        binaryResource.resourceValue=data;
        binaryResource.role=role;
        binaryResource.resourceName=resourceName;
        binaryResourceRepository.save(binaryResource);
        return id;

    }

    public long saveArrayResource(String resourceName, JSONArray data) {
        long id =   PageModel.elementHashCode(resourceName);
        ArrayResource arrayResource = new ArrayResource();
        arrayResource.arrayResourceId=id;
        arrayResource.name=resourceName;
        arrayResourceRepository.save(arrayResource);
        for (int i = 0; i < data.length(); i++) {
            String value = data.getString(i);
            ArrayEntry entry  = new ArrayEntry();
            entry.value = value;
            entry.arrayResource = arrayResource;
            arrayEntryRepository.save(entry);
        }
        return id;

    }
    public long saveTextResource(String resourceName,String data,String role) {
        long id =   PageModel.elementHashCode(resourceName);
        TextResource textResource = new TextResource();
        textResource.textResourceId=id;
        textResource.resourceValue=data;
        textResource.role=role;
        textResource.resourceName=resourceName;
        textResourceRepository.save(textResource);
        return id;

    }
}
