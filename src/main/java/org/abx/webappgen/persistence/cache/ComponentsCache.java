package org.abx.webappgen.persistence.cache;

import org.springframework.stereotype.Component;

@Component
public class ComponentsCache extends ResourceCache<ComponentCacheEntry> {


    public ComponentsCache() {
        super(1000);
    }

    public ComponentCacheEntry createEntry(long id){
        return new ComponentCacheEntry(id, this);
    }

}
