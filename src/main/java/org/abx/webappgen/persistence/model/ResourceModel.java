package org.abx.webappgen.persistence.model;

import org.abx.webappgen.persistence.dao.BinaryResourceRepository;
import org.abx.webappgen.persistence.dao.TextResourceRepository;
import org.abx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceModel {
    @Autowired
    TextResourceRepository textResourceRepository;
    @Autowired
    BinaryResourceRepository binaryResourceRepository;

    public String getTextResource(long id) {
        return textResourceRepository.findByTextResourceId(id).resourceValue;
    }

    public Pair<String, byte[]> getBinary(long id) {
        BinaryResource binaryResource = binaryResourceRepository.findByBinaryResourceId(id);
        return new Pair<>(binaryResource.resourceType, binaryResource.resourceValue);
    }
}
