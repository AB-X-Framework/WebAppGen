package org.abx.webappgen.persistence.cache;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class ComponentsCache extends ResourceCache<ComponentCacheEntry> {


    public ComponentsCache() {
        super(1000);
    }

    private ComponentCacheEntry createEntry(long id){
        return new ComponentCacheEntry(id, this);
    }

    public ComponentCacheEntry add(long id, JSONObject component){
        ComponentCacheEntry entry = createEntry(id);
        entry.component = component;
        add(id, createEntry(id));
        return entry;
    }

}
