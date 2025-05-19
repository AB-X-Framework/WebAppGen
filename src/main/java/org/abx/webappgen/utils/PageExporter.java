package org.abx.webappgen.utils;


import jakarta.annotation.PostConstruct;
import org.abx.util.StreamUtils;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.ResourceModel;
import org.abx.webappgen.persistence.dao.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;

@Component
public class PageExporter {
    @Autowired
    public PageRepository pageRepository;

    @Autowired
    public ComponentRepository componentRepository;

    @Autowired
    public ContainerRepository containerRepository;

    @Autowired
    public ElementRepository elementRepository;

    @Autowired
    public InnerComponentRepository innerComponentRepository;

    @Autowired
    public EnvValueRepository envValueRepository;

    @Autowired
    public TextResourceRepository textResourceRepository;

    @Autowired
    public BinaryResourceRepository binaryResourceRepository;


    @Autowired
    public ArrayResourceRepository arrayResourceRepository;

    @Autowired
    public ArrayEntryRepository arrayEntryRepository;


    @Autowired
    public MapEntryRepository mapEntryRepository;

    @Autowired
    public MapResourceRepository mapResourceRepository;

    @Autowired
    public MethodSpecRepository methodSpecRepository;

    @Autowired
    public ResourceModel resourceModel;

    public JSONObject getSpecs() {
        JSONObject specs = new JSONObject();
        specs.put("methods", getMethods());
        return specs;
    }

    public JSONArray getMethods(){
        throw new RuntimeException("Not implemented yet");
    }


}
