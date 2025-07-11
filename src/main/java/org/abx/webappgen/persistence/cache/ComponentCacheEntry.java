package org.abx.webappgen.persistence.cache;

import org.abx.webappgen.persistence.EnvListener;

public class ComponentCacheEntry implements EnvListener {
    private final ComponentsCache parent;
    private final long id;

    public ComponentCacheEntry(long id,ComponentsCache parent) {
        this.parent = parent;
        this.id = id;
    }

    @Override
    public void envChanged(){
        this.parent.remove(id);
    }
}
