package org.abx.webappgen.persistence;

import java.util.HashMap;
import java.util.Map;

public class ComponentSpecs {
    public String parent;
    public String localId;
    public String env;
    public Map<String, String> siblings;

    public ComponentSpecs(String parent,String env){
        this.parent = parent;
        this.env = env;
    }

    public ComponentSpecs child(String parent){
        return  new ComponentSpecs(parent,env);
    }
}
