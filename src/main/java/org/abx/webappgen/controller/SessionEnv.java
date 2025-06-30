package org.abx.webappgen.controller;

import java.util.HashMap;
import java.util.Map;

public class SessionEnv {

    private final Map<String, String> asMap;
    private String[] asText;

    public SessionEnv(String defaultEnv) {
        asMap = new HashMap<>();
        for (String keyPair : defaultEnv.split("\\|")) {
            if (keyPair.isBlank()) {
                continue;
            }
            String[] pair = keyPair.split("=");
            asMap.put(pair[0], pair[1]);
        }
        asText();
    }

    private void asText() {
        asText = new String[asMap.size() + 1];
        int i = 0;
        asText[0] = "";
        for (Map.Entry<String, String> entry : asMap.entrySet()) {
            asText[++i] = entry.getKey() + "=" + entry.getValue();
        }
    }

    public String get(String key) {
        return asMap.get(key);
    }

    public boolean matches(String env) {
        for (String key : asText) {
            if (key.equals(env)) {
                return true;
            }
        }
        return false;
    }

    public void set(String key, String value) {
        asMap.put(key, value);
        asText();
    }
}
