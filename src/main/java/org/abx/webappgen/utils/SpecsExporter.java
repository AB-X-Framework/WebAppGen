package org.abx.webappgen.utils;


import org.abx.webappgen.persistence.ResourceModel;
import org.abx.webappgen.persistence.dao.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecsExporter {
    @Autowired
    public UserRepository userRepository;

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

    /**
     * Create specs and saves it in
     *
     * @return
     */
    public void createSpecs(String specsFolder) {
        JSONObject specs = new JSONObject();
        specs.put("methods", getMethods(specsFolder));
        specs.put("users", createUsers(specsFolder));
    }


    public String createUsers(String specsFolder) {
        throw new RuntimeException("Not implemented yet");
    }

    public JSONArray getMethods(String specsFolder) {
        throw new RuntimeException("Not implemented yet");
    }


}
