package org.abx.webappgen.utils;

import org.abx.webappgen.persistence.dao.ComponentRepository;
import org.abx.webappgen.persistence.dao.EnvValueRepository;
import org.abx.webappgen.persistence.model.EnvValue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ElementUtils {

    public static final String Component = "component";
    public static final String Size = "size";
    @Autowired
    public ComponentRepository componentRepository;

    @Autowired
    public EnvValueRepository envValueRepository;

    public static long elementHashCode(String element) {
        return element.hashCode();
    }

    public  ArrayList<EnvValue> createEnvValues(JSONArray envValues) {
        ArrayList<EnvValue> env = new ArrayList<>();
        for (int i = 0; i < envValues.length(); i++) {
            JSONObject jsEnvValue = envValues.getJSONObject(i);
            env.add(createEnvValue(jsEnvValue));
        }
        return env;
    }
    public   EnvValue createEnvValue(JSONObject jsonEnvValue) {
        EnvValue envValue = new EnvValue();
        envValue.env = jsonEnvValue.getString("env");
        envValue.value = jsonEnvValue.get("value").toString();
        envValueRepository.save(envValue);
        return envValue;
    }


    public void saveComponent(JSONArray js, org.abx.webappgen.persistence.model.Component component) {
        componentRepository.save(component);
        componentRepository.flush();
        component.js = createEnvValues(js);
        componentRepository.save(component);
    }


}
