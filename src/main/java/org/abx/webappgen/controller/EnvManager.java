package org.abx.webappgen.controller;

import java.util.HashSet;
import java.util.Set;

public class EnvManager {

    private final Set<EnvListener> listeners;

    public EnvManager() {
        listeners = new HashSet<EnvListener>();
    }

    public void addEnvListener(EnvListener listener) {
        listeners.add(listener);
    }

    public void envChanged() {
        for (EnvListener listener : listeners) {
            listener.envChanged();
        }
    }
}
