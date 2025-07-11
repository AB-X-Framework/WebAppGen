package org.abx.webappgen;

import org.abx.webappgen.persistence.cache.ResourceCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidateCacheTest {

    @Test
    public void trimCacheTest() {
        ResourceCache<Long> cache = new ResourceCache<>(100);

        for (long i = 0; i <= 101; ++i) {
            cache.add(i, i);
        }
        for (long i = 0; i <= 101; ++i) {
            Long v = cache.get(i);
            if (i< 50){
                Assertions.assertNull(v);
            }else{
                Assertions.assertNotNull(v);
                Assertions.assertEquals(i,v);
            }
        }
    }

}
