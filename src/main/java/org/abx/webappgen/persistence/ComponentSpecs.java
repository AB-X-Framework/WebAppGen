package org.abx.webappgen.persistence;

import org.abx.webappgen.controller.SessionEnv;
import org.abx.webappgen.persistence.model.Component;

import java.util.Map;

public class ComponentSpecs {
    public String parent;
    public SessionEnv env;
    public Map<String, String> siblings;
    public Component component;

    public ComponentSpecs(String parent, SessionEnv env){
        this.parent = parent;
        this.env = env;
    }

    public ComponentSpecs child(String parent){
        return  new ComponentSpecs(parent,env);
    }
}
