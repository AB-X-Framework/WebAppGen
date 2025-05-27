package org.abx.webappgen.controller;

import java.util.Map;
import java.util.Properties;

public class EnvProperties extends Properties {

    private Map<String,String> baseline;

    private Map<String,String> session;

    private EnvProperties(Map<String,String> baseline, Map<String,String> session){
        this.baseline = baseline;
        this.session = session;
    }

    public boolean matches(String prop){
        if (prop.isEmpty()){
            return true;
        }
        String[] specs = prop.split("=");
        String key = specs[0];
        String value = specs[1];
        if (session.containsKey(key) && session.get(key).equals(value)){
            return true;
        }
        return baseline.containsKey(key) && baseline.get(key).equals(value);
    }
}
