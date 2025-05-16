package org.abx.webappgen.persistence;

import org.abx.webappgen.persistence.dao.BinaryResourceRepository;
import org.abx.webappgen.persistence.dao.TextResourceRepository;
import org.abx.util.Pair;
import org.abx.webappgen.persistence.model.BinaryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public Pair<String, byte[]> getBinaryResource(String resourceName) {
        BinaryResource binaryResource = binaryResourceRepository.findByBinaryResourceId(
                PageModel.elementHashCode(resourceName));
        return new Pair<>(binaryResource.resourceType, binaryResource.resourceValue);
    }

    public long saveBinaryResource(String resourceName,String resourceType, byte[] data) {
        long id =   PageModel.elementHashCode(resourceName);
        BinaryResource binaryResource = new BinaryResource();
        binaryResource.binaryResourceId=id;
        binaryResource.resourceType=resourceType;
        binaryResource.resourceValue=data;
        binaryResource.resourceName=resourceName;
        binaryResourceRepository.save(binaryResource);
        return id;

    }
}
