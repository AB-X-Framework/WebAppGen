package org.abx.webappgen.persistence.cache;

import org.abx.webappgen.persistence.EnvListener;
import org.json.JSONObject;

public class ComponentCacheEntry implements EnvListener {
    private final ComponentsCache parent;
    private final long id;
    public JSONObject component;
    public ComponentCacheEntry(long id,ComponentsCache parent) {
        this.parent = parent;
        this.id = id;
    }

    @Override
    public void envChanged(){
        this.parent.remove(id);
    }
}
