package org.abx.webappgen.persistence;

import java.util.HashMap;
import java.util.Map;

public class ComponentSpecs {
    public String parent;
    public String localId;
    public String env;
    public Map<String, String> siblings;

    public ComponentSpecs(){
        siblings = new HashMap<>();
    }
}
