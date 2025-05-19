package org.abx.webappgen.persistence;

import org.abx.webappgen.persistence.dao.BinaryResourceRepository;
import org.abx.webappgen.persistence.dao.TextResourceRepository;
import org.abx.util.Pair;
import org.abx.webappgen.persistence.model.BinaryResource;
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

    public String getTextResource(String resourceName) {
        return textResourceRepository.findByTextResourceId(
                PageModel.elementHashCode(resourceName)
        ).resourceValue;
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
}
