package org.abx.webappgen.controller;

import java.util.HashMap;
import java.util.Map;

public class SessionEnv {

    private Map<String, String> asMap;
    private String asText;

    public SessionEnv(String defaultEnv){
        asText = defaultEnv;
        asMap = new HashMap<>();
        for (String keyPair : asText.split("\\|")){
            if (keyPair.isBlank()) {
                continue;
            }
            String[] pair = keyPair.split("=");
            asMap.put(pair[0], pair[1]);
        }
    }

    public String get(String key) {
        return asMap.get(key);
    }

    public boolean matches(String env){
        return asText.contains(env);
    }

    public void set(String key, String value){
        asMap.put(key, value);
        StringBuilder sb= new StringBuilder();
        for (Map.Entry<String,String> keyPair : asMap.entrySet()) {
            sb.append(keyPair.getKey()).append("=").append(keyPair.getValue()).append("|");
        }
        asText = sb.toString();
    }
}
