package org.abx.webappgen.controller;

import java.util.HashMap;
import java.util.Map;

public class EnvProperties {

    private Map<String, String> baseline;

    private Map<String, String> session;

    private String str;

    private void recompile() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : baseline.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        for (Map.Entry<String, String> entry : session.entrySet()) {
            if (!baseline.containsKey(entry.getKey())) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
        }
        str = sb.toString();
    }

    public String asString() {
        return str;
    }

    private EnvProperties(Map<String, String> baseline) {
        this.baseline = baseline;
        this.session = new HashMap<>();
        recompile();
    }

    public void addProperty(String prop, String value) {
        session.put(prop, value);
        asString();
    }
}
